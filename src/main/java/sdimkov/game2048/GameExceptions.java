package sdimkov.game2048;

class GameOverException extends RuntimeException {}

class GameWonException extends GameOverException {}

class GameLostException extends GameOverException {}