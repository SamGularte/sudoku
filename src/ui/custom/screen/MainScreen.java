package ui.custom.screen;

import model.Space;
import service.BoardService;
import service.NotifierService;
import ui.custom.button.FinishGameButton;
import ui.custom.button.ResetButton;
import ui.custom.button.buttonCheckGameStatusButton;
import ui.custom.frame.MainFrame;
import ui.custom.input.NumberText;
import ui.custom.panel.MainPanel;
import ui.custom.panel.SudokuSector;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

import static service.EventEnum.CLEAR_SPACE;

public class MainScreen {
    private final static Dimension dimension = new Dimension(600, 600);

    private final BoardService boardSerice;
    private final NotifierService notifyService;

    private JButton finishGameButton;
    private JButton chackGameStatusButton;
    private JButton resetButton;

    public MainScreen(final Map<String, String> gameConfig) {
        this.boardSerice = new BoardService(gameConfig);
        this.notifyService = new NotifierService();
    }

    public void buildMainScreen(){
        JPanel mainPanel = new MainPanel(dimension);
        JFrame mainFrame = new MainFrame(dimension, mainPanel);
        for(int row = 0; row < 9; row += 3){
            var endRow = row + 2;
            for(int col = 0; col < 9; col += 3) {
                var endCol = col + 2;
                var spaces = getSpacesFromSector(boardSerice.getSpace(), col, endCol, row, endRow);
                mainPanel.add(generateSection(spaces));
            }
        }
        addResetButton(mainPanel);
        addShowGameStatusButton(mainPanel);
        addFinishGameButton(mainPanel);
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    private List<Space> getSpacesFromSector(List<List<Space>> spaces, final int initCol, final int endCol, final int initRow, final int endRow){
        List<Space> spaceSector = new ArrayList<>();
        for(int row = initRow; row <= endRow; row++){
            for(int col = initCol; col <= endCol; col++){
                spaceSector.add(spaces.get(col).get(row));
            }
        }

        return spaceSector;
    }

    private JPanel generateSection(final List<Space> spaces){
        List<NumberText> fields = new ArrayList<>(spaces.stream().map(NumberText::new).toList());
        fields.forEach(t -> notifyService.subscribe(CLEAR_SPACE, t));
        return new SudokuSector(fields);
    }

    private void addFinishGameButton(JPanel mainPanel) {
        finishGameButton = new FinishGameButton(e -> {
            if(boardSerice.gameIsFinished()){
                JOptionPane.showMessageDialog(null, "Parabens voce concluiu o jogo.");
                resetButton.setEnabled(false);
                chackGameStatusButton.setEnabled(false);
                finishGameButton.setEnabled(false);
            } else {
                JOptionPane.showMessageDialog(null, "Seu jogo contem alguma inconsistencia, ajuste e tente novamente.");
            }
        });
        mainPanel.add(finishGameButton);
    }

    private void addShowGameStatusButton(JPanel mainPanel) {
        chackGameStatusButton = new buttonCheckGameStatusButton(e -> {
            var hasErrors = boardSerice.hasErrors();
            var gameStatus = boardSerice.getStatus();

            var message = switch (gameStatus){
                case NON_STARTED -> "O jogo nao foi iniciado";
                case INCOMPLETE -> "O jogo esta incompleto";
                case COMPLETE -> "O jogo esta completo";
            };
            message += hasErrors ? "e contem erros" : "e nao contem erros";
            JOptionPane.showMessageDialog(null, message);
        });
        mainPanel.add(chackGameStatusButton);
    }

    private void addResetButton(JPanel mainPanel) {
        resetButton = new ResetButton(e -> {
            var dialogResult = JOptionPane.showConfirmDialog(null, "deseja realmente resetar o jogo?", "limpar o jogo", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(dialogResult == 0){
                boardSerice.reset();
                notifyService.notify(CLEAR_SPACE);
            }
        });
        mainPanel.add(resetButton);
    }
}
