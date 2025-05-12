document.addEventListener('DOMContentLoaded', () => {
    const API_URL = 'http://localhost:8080/game';
    const AUTH_URL = 'http://localhost:8080/auth';
    const USER_URL = 'http://localhost:8080/user';
    let currentGameId = null;
    let userId = null;
    let playerSymbol = null;
    let pollingInterval = null;

    // DOM-элементы
    const cells = document.querySelectorAll('.cell');
    const newGameBtn = document.getElementById('newGameBtn');
    const availableGamesBtn = document.getElementById('availableGamesBtn');
    const joinGameBtn = document.getElementById('joinGameBtn');
    const userInfoBtn = document.getElementById('userInfoBtn');
    const logoutBtn = document.getElementById('logoutBtn');
    const gameIdInput = document.getElementById('gameIdInput');
    const loginBtn = document.getElementById('loginBtn');
    const registerBtn = document.getElementById('registerBtn');
    const status = document.getElementById('status');
    const playerInfo = document.getElementById('player-info');
    const authStatus = document.getElementById('auth-status');
    const gameSection = document.getElementById('game-section');
    const authSection = document.getElementById('auth-section');
    const gameControls = document.getElementById('game-controls');
    const userLogin = document.getElementById('user-login');
    const notification = document.getElementById('notification');
    const notificationText = document.getElementById('notification-text');
    const notificationClose = document.getElementById('notification-close');
    const loginInput = document.getElementById('login');
    const passwordInput = document.getElementById('password');
    const modeModal = document.getElementById('mode-modal');
    const humanModeBtn = document.getElementById('humanModeBtn');
    const computerModeBtn = document.getElementById('computerModeBtn');
    const modeCloseBtn = document.getElementById('modeCloseBtn');
    const availableGamesModal = document.getElementById('available-games-modal');
    const availableGamesList = document.getElementById('available-games-list');
    const availableGamesCloseBtn = document.getElementById('availableGamesCloseBtn');
    const userInfoModal = document.getElementById('user-info-modal');
    const userInfoContent = document.getElementById('user-info-content');
    const userInfoCloseBtn = document.getElementById('userInfoCloseBtn');

    // Проверка авторизации и восстановление игры
    async function checkAuth() {
        const username = localStorage.getItem('username');
        const password = localStorage.getItem('password');
        const storedUserId = localStorage.getItem('userId');
        currentGameId = localStorage.getItem('currentGameId');

        if (username && password && storedUserId) {
            userId = storedUserId;
            authSection.style.display = 'none';
            gameSection.style.display = 'block';
            gameControls.style.display = 'flex';
            userLogin.textContent = `Игрок: ${username}`;
            status.textContent = 'Загрузка...';
            console.log('Found stored session for user:', userId);
            // Проверяем данные пользователя и восстанавливаем игру
            const userInfoFetched = await fetchUserInfo();
            if (!userInfoFetched) {
                console.warn('Failed to fetch user info, showing auth section');
                authSection.style.display = 'block';
                gameSection.style.display = 'none';
                gameControls.style.display = 'none';
                userLogin.textContent = '';
                localStorage.clear();
                status.textContent = 'Пожалуйста, войдите';
            }
        } else {
            console.log('No stored session, showing auth section');
            authSection.style.display = 'block';
            gameSection.style.display = 'none';
            gameControls.style.display = 'none';
            userLogin.textContent = '';
            localStorage.clear();
            status.textContent = 'Пожалуйста, войдите';
        }
    }

    // Отрисовка игрового поля
    function renderBoard(field) {
        if (!field || !Array.isArray(field) || field.length !== 3) {
            showNotification('Ошибка: неверное игровое поле');
            return;
        }
        cells.forEach(cell => {
            const x = parseInt(cell.dataset.x);
            const y = parseInt(cell.dataset.y);
            if (Array.isArray(field[x]) && field[x][y] !== undefined) {
                const value = field[x][y];
                cell.textContent = value === 1 ? 'X' : value === -1 ? 'O' : '';
            } else {
                cell.textContent = '';
            }
        });
    }

    // Отображение информации об игроках
    function renderPlayerInfo(symbols, mode) {
        if (!symbols || !symbols[userId]) {
            playerInfo.textContent = '';
            return;
        }
        if (mode === 'computer') {
            playerInfo.textContent = `Вы: ${symbols[userId] || 'X'}, Компьютер: ${symbols['computer'] || 'O'}`;
        } else {
            const opponentId = Object.keys(symbols).find(id => id && id !== userId && id !== 'computer' && id !== 'xPlayerId' && id !== 'oPlayerId');
            playerInfo.textContent = `Вы: ${symbols[userId] || 'X'}, Противник: ${opponentId ? symbols[opponentId] : 'ждём игрока'}`;
        }
    }

    // Показ уведомления
    function showNotification(message, duration = 3000) {
        notificationText.textContent = message;
        notification.classList.add('active');
        setTimeout(() => {
            notification.classList.remove('active');
        }, duration);
    }

    // Проверка победителя на основе игрового поля
    function checkWinnerFromBoard(board) {
        // Проверка строк
        for (let i = 0; i < 3; i++) {
            if (board[i][0] === board[i][1] && board[i][1] === board[i][2] && board[i][0] !== 0) {
                return board[i][0] === 1 ? 'X' : 'O';
            }
        }
        // Проверка столбцов
        for (let j = 0; j < 3; j++) {
            if (board[0][j] === board[1][j] && board[1][j] === board[2][j] && board[0][j] !== 0) {
                return board[0][j] === 1 ? 'X' : 'O';
            }
        }
        // Проверка диагоналей
        if (board[0][0] === board[1][1] && board[1][1] === board[2][2] && board[0][0] !== 0) {
            return board[0][0] === 1 ? 'X' : 'O';
        }
        if (board[0][2] === board[1][1] && board[1][1] === board[2][0] && board[0][2] !== 0) {
            return board[0][2] === 1 ? 'X' : 'O';
        }
        return null;
    }

    // Проверка окончания игры и управление клетками
    function checkGameEnd(gameState, board) {
        const { status: gameStatus, mode, symbols, playerId } = gameState;

        // Определяем символ текущего хода
        let currentPlayerSymbol = playerId === symbols.xPlayerId ? 'X' : playerId === symbols.oPlayerId ? 'O' : null;

        if (gameStatus.includes('Победа')) {
            // Проверяем победителя по playerId
            let winnerSymbol;
            if (playerId === symbols.xPlayerId) {
                winnerSymbol = 'X';
            } else if (playerId === symbols.oPlayerId) {
                winnerSymbol = 'O';
            } else {
                // Если playerId не указывает на игрока, проверяем игровое поле
                winnerSymbol = checkWinnerFromBoard(board);
                if (!winnerSymbol) {
                    console.warn('Could not determine winner from board or playerId:', { playerId, symbols, board });
                    winnerSymbol = 'X'; // Fallback, чтобы избежать пустого статуса
                }
            }
            status.textContent = `Выиграли ${winnerSymbol}`;
            cells.forEach(cell => cell.classList.add('disabled'));
            currentGameId = null;
            playerSymbol = null;
            localStorage.removeItem('currentGameId');
            stopGameStatePolling();
            showNotification(`Игра завершена: Выиграли ${winnerSymbol}`);
        } else if (gameStatus.includes('Ничья')) {
            status.textContent = 'Ничья';
            cells.forEach(cell => cell.classList.add('disabled'));
            currentGameId = null;
            playerSymbol = null;
            localStorage.removeItem('currentGameId');
            stopGameStatePolling();
            showNotification('Игра завершена: Ничья');
        } else if (gameStatus === 'Ожидание игроков') {
            status.textContent = 'Ожидание второго игрока';
            cells.forEach(cell => cell.classList.add('disabled'));
            startGameStatePolling();
        } else {
            // Показываем, чей ход
            status.textContent = currentPlayerSymbol ? `Ход игрока ${currentPlayerSymbol}` : gameStatus;

            // Активируем клетки только для игрока, чей ход
            if (mode === 'human' && playerId === userId) {
                cells.forEach(cell => {
                    if (!cell.textContent) {
                        cell.classList.remove('disabled');
                    } else {
                        cell.classList.add('disabled');
                    }
                });
            } else if (mode === 'computer' && playerId === userId) {
                cells.forEach(cell => {
                    if (!cell.textContent) {
                        cell.classList.remove('disabled');
                    } else {
                        cell.classList.add('disabled');
                    }
                });
            } else {
                cells.forEach(cell => cell.classList.add('disabled'));
            }
            startGameStatePolling();
        }
        renderPlayerInfo(symbols, mode);
    }

    // Создание Basic Auth заголовка
    function createBasicAuthHeader(username, password) {
        if (!username || !password) {
            console.error('Missing username or password for Basic Auth');
            return null;
        }
        const credentials = `${username}:${password}`;
        const encodedCredentials = btoa(credentials);
        console.log('Basic Auth Header created:', `Basic ${encodedCredentials}`);
        return `Basic ${encodedCredentials}`;
    }

    // Авторизация
    async function login(username, password) {
        const authHeader = createBasicAuthHeader(username, password);
        if (!authHeader) {
            authStatus.textContent = 'Ошибка: Логин и пароль не могут быть пустыми';
            return;
        }
        try {
            console.log('Sending login request with header:', authHeader);
            const response = await fetch(`${AUTH_URL}/login`, {
                method: 'POST',
                headers: {
                    'Authorization': authHeader
                }
            });
            const data = await response.json();
            if (response.ok) {
                console.log('Login response:', data);
                localStorage.setItem('username', username);
                localStorage.setItem('password', password);
                const userInfoFetched = await fetchUserInfo();
                if (!userInfoFetched) {
                    authStatus.textContent = 'Ошибка: Не удалось получить данные пользователя';
                    localStorage.clear();
                    return;
                }
                authSection.style.display = 'none';
                gameSection.style.display = 'block';
                gameControls.style.display = 'flex';
                userLogin.textContent = `Игрок: ${username}`;
                authStatus.textContent = '';
                loginInput.value = '';
                passwordInput.value = '';
                showNotification('Вход успешен!');
            } else {
                authStatus.textContent = `Ошибка входа: ${data.error || 'Неизвестная ошибка'}`;
                console.error('Login failed:', data.error);
                showNotification(`Ошибка входа: ${data.error || 'Неизвестная ошибка'}`);
            }
        } catch (error) {
            authStatus.textContent = 'Ошибка сети';
            console.error('Login error:', error);
            showNotification('Ошибка сети');
        }
    }

    // Регистрация
    async function register(username, password) {
        const authHeader = createBasicAuthHeader(username, password);
        if (!authHeader) {
            authStatus.textContent = 'Ошибка: Логин и пароль не могут быть пустыми';
            return;
        }
        try {
            console.log('Sending register request with header:', authHeader);
            const response = await fetch(`${AUTH_URL}/signup`, {
                method: 'POST',
                headers: {
                    'Authorization': authHeader,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ login: username, password })
            });
            const data = await response.json();
            if (response.ok) {
                showNotification('Регистрация успешна! Выполняется вход...');
                await login(username, password);
            } else {
                authStatus.textContent = `Ошибка регистрации: ${data.error || 'Неизвестная ошибка'}`;
                console.error('Register failed:', data.error);
                showNotification(`Ошибка регистрации: ${data.error || 'Неизвестная ошибка'}`);
            }
        } catch (error) {
            authStatus.textContent = 'Ошибка сети';
            console.error('Register error:', error);
            showNotification('Ошибка сети');
        }
    }

    // Получение информации о пользователе
    async function fetchUserInfo() {
        const username = localStorage.getItem('username');
        const password = localStorage.getItem('password');
        const authHeader = createBasicAuthHeader(username, password);
        if (!authHeader) {
            showNotification('Ошибка: Необходима авторизация');
            return false;
        }
        try {
            console.log('Fetching current user info with header:', authHeader);
            const response = await fetch(`${USER_URL}/me`, {
                method: 'GET',
                headers: { 'Authorization': authHeader }
            });
            if (!response.ok) {
                const errorData = await response.json();
                console.error('Fetch user info failed:', errorData.error || response.statusText);
                showNotification(`Ошибка получения профиля: ${errorData.error || response.statusText}`);
                return false;
            }
            const data = await response.json();
            userId = data.id;
            localStorage.setItem('userId', userId);
            localStorage.setItem('currentGameId', data.currentGameId);
            userInfoContent.innerHTML = `
                <p>ID: ${data.id}</p>
                <p>Логин: ${data.login}</p>
                <p>Рейтинг: ${data.rating}</p>
            `;
            console.log('User info fetched, userId set to:', userId);
            if (data.currentGameId) {
                currentGameId = data.currentGameId;
                console.log(`Restoring game ${currentGameId} for user ${userId}`);
                await fetchGameState();
            } else {
                status.textContent = 'Нажмите "Новая игра" для начала';
            }
            return true;
        } catch (error) {
            console.error('Fetch user info error:', error);
            showNotification('Ошибка сети при получении профиля');
            return false;
        }
    }

    // Создание новой игры
    async function startNewGame(mode) {
        const username = localStorage.getItem('username');
        const password = localStorage.getItem('password');
        const authHeader = createBasicAuthHeader(username, password);

        if (!authHeader) {
            showNotification('Пожалуйста, авторизуйтесь!');
            authSection.style.display = 'block';
            gameSection.style.display = 'none';
            gameControls.style.display = 'none';
            return;
        }

        try {
            console.log(`Creating new game with mode: ${mode}`);
            const response = await fetch(`${API_URL}/new`, {
                method: 'POST',
                headers: {
                    'Authorization': authHeader,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ mode })
            });
            const data = await response.json();
            console.log('New game response:', data);

            if (response.ok) {
                if (!data.id) {
                    throw new Error('Game ID not provided in response');
                }
                currentGameId = data.id;
                localStorage.setItem('currentGameId', currentGameId);
                playerSymbol = data.xPlayer === userId ? 'X' : (data.oPlayer === userId ? 'O' : 'X');
                showNotification(`Игра создана! ID: ${data.id}, Режим: ${mode === 'human' ? 'с человеком' : 'с компьютером'}`);

                if (mode === 'computer') {
                    console.log('Fetching game state for computer mode');
                    await fetchGameState();
                } else {
                    console.log('Joining game in human mode');
                    await joinGame(data.id);
                }
            } else {
                const errorMsg = data.error || 'Неизвестная ошибка';
                showNotification(`Ошибка создания игры: ${errorMsg}`);
                console.error('New game failed:', errorMsg);
            }
        } catch (error) {
            showNotification(`Ошибка создания игры: ${error.message}`);
            console.error('Start new game error:', error);
        }
    }

    // Подключение к игре
    async function joinGame(gameId) {
        const username = localStorage.getItem('username');
        const password = localStorage.getItem('password');
        const authHeader = createBasicAuthHeader(username, password);

        if (!authHeader) {
            showNotification('Пожалуйста, авторизуйтесь!');
            return;
        }

        const idToJoin = gameId || gameIdInput.value.trim();
        if (!idToJoin) {
            showNotification('Введите ID игры или создайте новую!');
            return;
        }

        if (!userId) {
            console.warn('userId is not set, attempting to fetch user info');
            const userInfoFetched = await fetchUserInfo();
            if (!userInfoFetched) {
                showNotification('Ошибка: ID пользователя не определён');
                return;
            }
        }

        try {
            console.log(`Joining game ${idToJoin} with userId: ${userId}`);
            const response = await fetch(`${API_URL}/${idToJoin}/join`, {
                method: 'POST',
                headers: { 'Authorization': authHeader }
            });

            console.log('Response status:', response.status);
            const data = await response.json();
            console.log('Response data:', data);

            if (response.ok) {
                currentGameId = idToJoin;
                localStorage.setItem('currentGameId', currentGameId);
                playerSymbol = data.xPlayer === userId ? 'X' : (data.oPlayer === userId ? 'O' : 'X');
                gameIdInput.value = '';
                const gameBoard = data.gameBoard || [[0, 0, 0], [0, 0, 0], [0, 0, 0]];
                renderBoard(gameBoard);
                checkGameEnd({
                    status: data.status || 'Ожидание игроков',
                    mode: data.mode === 'Игра с человеком' ? 'human' : 'computer',
                    symbols: {
                        [data.xPlayer]: 'X',
                        [data.oPlayer]: 'O',
                        xPlayerId: data.xPlayer,
                        oPlayerId: data.oPlayer
                    },
                    playerId: data.playerId
                }, gameBoard);
            } else {
                const errorMsg = data.error || 'Неизвестная ошибка';
                console.error('Join game failed:', errorMsg);
                if (errorMsg.includes('Пользователь уже участвует')) {
                    currentGameId = idToJoin;
                    localStorage.setItem('currentGameId', currentGameId);
                    await fetchGameState();
                    showNotification(`Вы уже в игре ${idToJoin}`);
                } else {
                    showNotification(`Ошибка подключения: ${errorMsg}`);
                }
            }
        } catch (error) {
            console.error('Join game error:', error);
            showNotification('Ошибка подключения к игре: ' + error.message);
        }
    }

    // Получение состояния игры
    async function fetchGameState() {
        const username = localStorage.getItem('username');
        const password = localStorage.getItem('password');
        const authHeader = createBasicAuthHeader(username, password);
        if (!authHeader || !currentGameId) {
            showNotification('Подключитесь к игре!');
            return;
        }
        try {
            console.log(`Fetching game state for game ${currentGameId}`);
            const response = await fetch(`${API_URL}/${currentGameId}`, {
                method: 'GET',
                headers: { 'Authorization': authHeader }
            });
            const data = await response.json();
            console.log('Game state response:', data);
            if (response.ok) {
                const gameBoard = data.gameBoard || [[0, 0, 0], [0, 0, 0], [0, 0, 0]];
                renderBoard(gameBoard);
                checkGameEnd({
                    status: data.status || 'Ожидание хода',
                    mode: data.mode === 'Игра с человеком' ? 'human' : 'computer',
                    symbols: {
                        [data.xPlayer]: 'X',
                        [data.oPlayer]: 'O',
                        xPlayerId: data.xPlayer,
                        oPlayerId: data.oPlayer
                    },
                    playerId: data.playerId
                }, gameBoard);
            } else {
                showNotification(`Ошибка получения игры: ${data.error || 'Неизвестная ошибка'}`);
                console.error('Fetch game state failed:', data.error);
                localStorage.removeItem('currentGameId');
                currentGameId = null;
                status.textContent = 'Нажмите "Новая игра" для начала';
            }
        } catch (error) {
            showNotification('Ошибка сети');
            console.error('Fetch game state error:', error);
            localStorage.removeItem('currentGameId');
            currentGameId = null;
            status.textContent = 'Нажмите "Новая игра" для начала';
        }
    }

    // Периодический опрос состояния игры
    function startGameStatePolling() {
        if (pollingInterval) return;
        console.log('Started game state polling');
        pollingInterval = setInterval(async () => {
            if (!currentGameId) {
                stopGameStatePolling();
                return;
            }
            await fetchGameState();
        }, 1000);
    }

    // Остановка опроса
    function stopGameStatePolling() {
        if (pollingInterval) {
            clearInterval(pollingInterval);
            pollingInterval = null;
            console.log('Stopped game state polling');
        }
    }

    // Ход в игре
    async function makeMove(x, y) {
        const username = localStorage.getItem('username');
        const password = localStorage.getItem('password');
        const authHeader = createBasicAuthHeader(username, password);
        if (!authHeader || !currentGameId) {
            showNotification('Подключитесь к игре!');
            return;
        }
        try {
            console.log(`Making move in game ${currentGameId} at (${x}, ${y})`);
            const response = await fetch(`${API_URL}/${currentGameId}`, {
                method: 'POST',
                headers: {
                    'Authorization': authHeader,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ move: [x, y] })
            });
            const data = await response.json();
            console.log('Move response:', data);
            if (response.ok) {
                const gameBoard = data.gameBoard || [[0, 0, 0], [0, 0, 0], [0, 0, 0]];
                renderBoard(gameBoard);
                checkGameEnd({
                    status: data.status || 'Ожидание хода',
                    mode: data.mode === 'Игра с человеком' ? 'human' : 'computer',
                    symbols: {
                        [data.xPlayer]: 'X',
                        [data.oPlayer]: 'O',
                        xPlayerId: data.xPlayer,
                        oPlayerId: data.oPlayer
                    },
                    playerId: data.playerId
                }, gameBoard);
            } else {
                showNotification(`Ошибка хода: ${data.error || 'Неизвестная ошибка'}`);
                console.error('Move failed:', data.error);
            }
        } catch (error) {
            showNotification('Ошибка сети при выполнении хода');
            console.error('Make move error:', error);
        }
    }

    // Получение доступных игр
    async function fetchAvailableGames() {
        const username = localStorage.getItem('username');
        const password = localStorage.getItem('password');
        const authHeader = createBasicAuthHeader(username, password);
        if (!authHeader) {
            showNotification('Пожалуйста, авторизуйтесь!');
            return;
        }
        try {
            const response = await fetch(`${API_URL}/available`, {
                method: 'GET',
                headers: { 'Authorization': authHeader }
            });
            const data = await response.json();
            if (response.ok) {
                availableGamesList.innerHTML = '';
                if (data.length === 0) {
                    availableGamesList.innerHTML = '<p>Нет доступных игр</p>';
                } else {
                    data.forEach(game => {
                        const gameItem = document.createElement('div');
                        gameItem.className = 'game-item';
                        gameItem.textContent = `Игра ${game.id}`;
                        gameItem.addEventListener('click', () => {
                            availableGamesModal.classList.remove('active');
                            joinGame(game.id);
                        });
                        availableGamesList.appendChild(gameItem);
                    });
                }
                availableGamesModal.classList.add('active');
            } else {
                showNotification(`Ошибка получения игр: ${data.error || 'Неизвестная ошибка'}`);
                console.error('Fetch available games failed:', data.error);
            }
        } catch (error) {
            showNotification('Ошибка сети');
            console.error('Fetch available games error:', error);
        }
    }

    // События
    loginBtn.addEventListener('click', async () => {
        const username = loginInput.value.trim();
        const password = passwordInput.value.trim();
        if (!username || !password) {
            authStatus.textContent = 'Введите логин и пароль';
            return;
        }
        await login(username, password);
    });

    registerBtn.addEventListener('click', async () => {
        const username = loginInput.value.trim();
        const password = passwordInput.value.trim();
        if (!username || !password) {
            authStatus.textContent = 'Введите логин и пароль';
            return;
        }
        await register(username, password);
    });

    logoutBtn.addEventListener('click', () => {
        localStorage.clear();
        currentGameId = null;
        userId = null;
        playerSymbol = null;
        authSection.style.display = 'block';
        gameSection.style.display = 'none';
        gameControls.style.display = 'none';
        userLogin.textContent = '';
        status.textContent = 'Пожалуйста, войдите';
        authStatus.textContent = '';
        renderBoard([[0, 0, 0], [0, 0, 0], [0, 0, 0]]);
        stopGameStatePolling();
        showNotification('Вы вышли из аккаунта');
    });

    newGameBtn.addEventListener('click', () => {
        modeModal.classList.add('active');
    });

    humanModeBtn.addEventListener('click', () => {
        modeModal.classList.remove('active');
        startNewGame('human');
    });

    computerModeBtn.addEventListener('click', () => {
        modeModal.classList.remove('active');
        startNewGame('computer');
    });

    modeCloseBtn.addEventListener('click', () => {
        modeModal.classList.remove('active');
    });

    availableGamesBtn.addEventListener('click', fetchAvailableGames);

    availableGamesCloseBtn.addEventListener('click', () => {
        availableGamesModal.classList.remove('active');
    });

    joinGameBtn.addEventListener('click', () => joinGame());

    userInfoBtn.addEventListener('click', () => {
        fetchUserInfo();
        userInfoModal.classList.add('active');
    });

    userInfoCloseBtn.addEventListener('click', () => {
        userInfoModal.classList.remove('active');
    });

    cells.forEach(cell => {
        cell.addEventListener('click', async () => {
            if (cell.classList.contains('disabled')) return;
            const x = parseInt(cell.dataset.x);
            const y = parseInt(cell.dataset.y);
            await makeMove(x, y);
        });
    });

    notificationClose.addEventListener('click', () => {
        notification.classList.remove('active');
    });

    // Проверка авторизации при загрузке
    checkAuth();
});