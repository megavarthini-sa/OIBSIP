import javax.swing.*;

public class Main {
    private final GameModel model;
    private final GameView view;
    public Main() 
    {
        this.model = new GameModel();
        this.view = new GameView();
        view.addGuessListener(e -> handleGuess());
        view.addDifficultyListener(e -> handleDifficultyChange());
        view.addPlayAgainListener(e -> handlePlayAgain());
        model.startNewRound();
        resetUIPresentation("New round started! Make your first move.");
        view.setVisible(true);
    }
    private void resetUIPresentation(String initialMessage) 
    {
        GameModel.Difficulty diff = model.getCurrentDifficulty();
        view.updateStatusText(diff.range, model.getCurrentAttempts(), diff.maxAttempts);
        view.showFeedbackInfo(initialMessage);
        view.clearGuessInput();
        view.setGameControlsEnabled(true);
    }
    private void handleGuess() 
    {
        try 
        {
            int guess = Integer.parseInt(view.getGuessInput());
            model.incrementAttempts();
            GameModel.GuessResult result = model.checkGuess(guess);
            GameModel.Difficulty diff = model.getCurrentDifficulty();
            view.updateStatusText(diff.range, model.getCurrentAttempts(), diff.maxAttempts);
            switch (result) 
            {
                case INVALID_RANGE : {
                    view.showFeedbackError("Out of range! Guess between 1 and " + diff.range);
                    return;
                }
                case CORRECT : {
                    view.showFeedbackSuccess("Correct! You got it!");
                    view.appendHistory("Round " + model.getRoundCount() + " — guessed in " + model.getCurrentAttempts() + " attempts (" + diff.name() + ")\n");
                    view.setGameControlsEnabled(false);
                    return;
                }
                case TOO_HIGH : view.showFeedbackWarning("Too High!");
                                break;
                case TOO_LOW : view.showFeedbackWarning("Too Low!");
                                break;
            }
            if (model.isGameOver()) 
            {
                view.showFeedbackError("You Lost! The number was " + model.getRandomNumber() + ".");
                view.appendHistory("Round " + model.getRoundCount() + " — Failed after " + diff.maxAttempts + " attempts (" + diff.name() + ")\n");
                view.setGameControlsEnabled(false);
            } 
            else 
            {
                view.clearGuessInput();
                view.focusGuessInput();
            }

        } 
        catch (NumberFormatException ex) 
        {
            view.showFeedbackError("Please enter a valid integer!");
        }
    }
    private void handleDifficultyChange() 
    {
        int index = view.getSelectedDifficultyIndex();
        if (index == 0) model.setDifficulty(GameModel.Difficulty.EASY);
        else if (index == 1) model.setDifficulty(GameModel.Difficulty.MEDIUM);
        else model.setDifficulty(GameModel.Difficulty.HARD);
        model.startNewRound();
        resetUIPresentation("Difficulty changed. New round initialized!");
    }
    private void handlePlayAgain() 
    {
        model.advanceToNextRound();
        resetUIPresentation("New round started! Make your first move.");
    }
    public static void main(String[] args) 
    {
        SwingUtilities.invokeLater(Main::new);
    }
}