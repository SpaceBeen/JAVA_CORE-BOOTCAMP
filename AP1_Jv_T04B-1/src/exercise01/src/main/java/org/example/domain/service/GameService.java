package org.example.domain.service;

import org.example.domain.model.CurrentGame;
import org.example.domain.model.GameMode;

import java.util.List;
import java.util.UUID;

public interface GameService {
    CurrentGame createGame(UUID userId, GameMode mode);
    List<CurrentGame> getAvailableGames();
    CurrentGame joinGame(UUID gameId, UUID userId);
    CurrentGame updateGame(UUID gameId, UUID userId, int x, int y);
    CurrentGame getGame(UUID gameId);
}