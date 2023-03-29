package fr.projet.type;

/**
 * Definiton d'un type pour identifier les messages
 */
public enum MessageType {
    //Uniquement pour le joueur
    GAME_JOIN,
    GAME_SPECIAL,
    GAME_LOSE,

    //Uniquement pour le serveur
    GAME_WIN,
    GAME_START,
    GAME_END,
    GAME_PLAYER_DISCONNECT,
    GAME_DENY,
    GAME_PLAYER_LIST,
    GAME_INFO,
    GAME_JOIN_SUCCESS,

    //Pour les deux
    GAME_SEND_WORD,
    GAME_FORCE_DISCONNECT
}
