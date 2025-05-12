document.addEventListener('DOMContentLoaded', () => {
    const API_URL = 'http://localhost:8080/game';
    const AUTH_URL = 'http://localhost:8080/auth';
    let currentGameId = null;
    let authHeader = null;
    let currentLogin = null;

    const cells = document.querySelectorAll('.cell');
    const newGameBtn = document.getElementById('newGameBtn');
    const loginBtn = document.getElementById('loginBtn');
    const registerBtn = document.getElementById('registerBtn');
    const logoutBtn = document.getElementById('logoutBtn');
    const status = document.getElementById('status');
    const authStatus = document.getElementById('auth-status');
    const gameSection = document.getElementById('game-section');
    const authSection = document.getElementById('auth-section');
    const userLogin = document.getElementById('user-login');
    const notification = document.getElementById('notification');
    const notificationText = document.getElementById('notification-text');
    const notificationClose = document.getElementById('notification-close');

    // Проверка на null
    console.log("DOM elements:", { authStatus, userLogin, notificationText });
    if (!authStatus || !userLogin || !notificationText) {
        console.error("One or more DOM elements are missing:", {
            authStatus: !!authStatus,
            userLogin: !!userLogin,
            notificationText: !!notificationText
        });
    }

    function renderBoard(board) {
        cells.forEach(cell => {
            const x = parseInt(cell.dataset.x);
            const y = parseInt(cell.dataset.y);
            const value = board[x][y];
            cell.textContent = value === 1 ? 'X' : value === -1 ? 'O' : '';
        });
    }

    async function updateGame() {
        if (!currentGameId) return;
        try {
            const response = await fetch(`${API_URL}/${currentGameId}`, {
                method: 'POST',
                headers: { 'Authorization': authHeader }
            });
            if (!response.ok) throw new Error('Failed to fetch game state');
            const gameState = await response.json();
            renderBoard(gameState.gameBoard);
            status.textContent = gameState.status;
            checkGameEnd(gameState); // Проверяем, закончилась ли игра
        } catch (error) {
            console.error('Error updating game:', error);
            status.textContent = 'Ошибка при обновлении игры';
        }
    }

    function showNotification(message) {
        if (notificationText) {
            notificationText.textContent = message;
            notification.classList.add('active');
            setTimeout(() => {
                notification.classList.remove('active');
            }, 3000);
        } else {
            console.warn('Notification text element is null');
        }
    }

    // Функция для проверки окончания игры и отключения ячеек
    function checkGameEnd(gameState) {
        // Предполагаем, что статус игры содержит информацию об окончании
        // Например, "Player X wins", "Player O wins" или "Draw"
        const statusText = gameState.status.toLowerCase();
        if (statusText.includes('wins') || statusText.includes('draw')) {
            disableRemainingCells(gameState.gameBoard);
        }
    }

    // Функция для отключения оставшихся свободных ячеек
    function disableRemainingCells(board) {
        cells.forEach(cell => {
            const x = parseInt(cell.dataset.x);
            const y = parseInt(cell.dataset.y);
            const value = board[x][y];
            if (value === 0) { // Если ячейка пуста (значение 0)
                cell.classList.add('disabled');
            }
        });
    }

    // Функция для сброса состояния ячеек
    function resetCells() {
        cells.forEach(cell => {
            cell.classList.remove('disabled');
        });
    }

    async function login(login, password) {
        authHeader = 'Basic ' + btoa(`${login}:${password}`);
        try {
            const response = await fetch(`${AUTH_URL}/login`, {
                method: 'POST',
                headers: { 'Authorization': authHeader }
            });
            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Ошибка авторизации: ${response.status} - ${errorText}`);
            }
            const userId = await response.text();
            if (authStatus) authStatus.textContent = '';
            gameSection.style.display = 'block';
            authSection.style.display = 'none';
            currentLogin = login;
            if (userLogin) userLogin.textContent = currentLogin;
            showNotification('Вход выполнен успешно!');
            console.log('Logged in as:', userId);
            return true;
        } catch (error) {
            console.error('Login error:', error);
            showNotification(error.message);
            return false;
        }
    }

    async function register(username, password) {
        try {
            const response = await fetch(`${AUTH_URL}/register`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ login: username, password: password })
            });
            if (!response.ok) {
                const errorData = await response.text();
                throw new Error(`Ошибка регистрации: ${response.status} - ${errorData}`);
            }
            const result = await response.text();
            showNotification('Регистрация успешна! Выполняется вход...');
            document.getElementById('login').value = '';
            document.getElementById('password').value = '';
            await login(username, password);
            return true;
        } catch (error) {
            console.error('Register error:', error);
            showNotification(error.message);
            return false;
        }
    }

    loginBtn.addEventListener('click', async () => {
        const loginInput = document.getElementById('login').value;
        const passwordInput = document.getElementById('password').value;
        await login(loginInput, passwordInput);
    });

    registerBtn.addEventListener('click', async () => {
        const loginInput = document.getElementById('login').value;
        const passwordInput = document.getElementById('password').value;
        await register(loginInput, passwordInput);
    });

    logoutBtn.addEventListener('click', () => {
        authHeader = null;
        currentLogin = null;
        currentGameId = null;
        authSection.style.display = 'block';
        gameSection.style.display = 'none';
        if (userLogin) userLogin.textContent = '';
        status.textContent = 'Нажмите "Новая игра" для начала';
        if (authStatus) authStatus.textContent = '';
        resetCells(); // Сбрасываем состояние ячеек при выходе
    });

    notificationClose.addEventListener('click', () => {
        notification.classList.remove('active');
    });

    newGameBtn.addEventListener('click', async () => {
        if (!authHeader) {
            alert('Пожалуйста, авторизуйтесь!');
            return;
        }
        try {
            const response = await fetch(`${API_URL}/new`, {
                method: 'POST',
                headers: { 'Authorization': authHeader }
            });
            if (!response.ok) throw new Error(`Failed to create new game: ${response.status}`);
            const gameState = await response.json();
            currentGameId = gameState.id;
            renderBoard(gameState.gameBoard);
            status.textContent = gameState.status;
            resetCells(); // Сбрасываем состояние ячеек при новой игре
        } catch (error) {
            console.error('Error creating new game:', error);
            status.textContent = 'Ошибка при создании игры: ' + error.message;
        }
    });

    cells.forEach(cell => {
        cell.addEventListener('click', async () => {
            if (!currentGameId) {
                alert('Сначала начните новую игру!');
                return;
            }
            if (cell.classList.contains('disabled')) {
                return; // Пропускаем клик, если ячейка уже неактивна
            }
            const x = parseInt(cell.dataset.x);
            const y = parseInt(cell.dataset.y);
            try {
                const response = await fetch(`${API_URL}/${currentGameId}`, {
                    method: 'POST',
                    headers: {
                        'Authorization': authHeader,
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ move: [x, y] })
                });
                if (!response.ok) {
                    const errorText = await response.text();
                    throw new Error(`Failed to make move: ${response.status} - ${errorText}`);
                }
                const gameState = await response.json();
                renderBoard(gameState.gameBoard);
                status.textContent = gameState.status;
                checkGameEnd(gameState); // Проверяем, закончилась ли игра после хода
            } catch (error) {
                console.error('Error making move:', error);
                status.textContent = 'Ошибка хода: ' + error.message;
            }
        });
    });
});