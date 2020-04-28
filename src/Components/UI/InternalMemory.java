package Components.UI;

import Components.Memory;
import Tools.Word;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import static java.lang.Integer.parseInt;
import static java.util.Arrays.copyOfRange;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

public class InternalMemory {
    public static final String SPACE = " ";
    private JPanel internalMemory;
    private JButton enterInput;
    private JTextField blockNumberInput;
    private JTable block;
    private JScrollPane blockScroll;
    private JLabel blockText;
    private final Memory memory;

    public InternalMemory(Memory memory) {
        this.memory = memory;
        enterInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String input = blockNumberInput.getText();
                Integer blockNumber = parseInt(input);
                if(blockNumber != null && isNumeric(input) && blockNumber <= 16 && blockNumber >= 0) {
                    try {
                        setTable(blockNumber);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void setTable(int blockNumber) throws Exception {
        Word[] content = memory.getBlock(blockNumber);
        List<String> values = stream(content).map(Word::getTableHEXFormat).collect(toList());
        Object[] columnNames = new Object[6];
        String[][] tableOfValues = new String[256][6];
        for (int i = 1; i < 7; i++) {
            columnNames[i - 1] = (Integer) i;
        }
        for (int i = 0; i < values.size(); i++) {
            String value = values.get(i);
            String[] word = value.split(SPACE).clone();
            tableOfValues[i] = copyOfRange(word, 0, 6);
        }
        DefaultTableModel model = new DefaultTableModel(tableOfValues, columnNames);
        block.setModel(model);
        block.setFillsViewportHeight(true);
        blockScroll.setViewportView(block);
    }

    private boolean isNumeric(String strNum) {
        if (strNum == null) {
            blockText.setText("EMPTY");
            return false;
        }
        try {
            int d = Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            blockText.setText("NOT NUMBER");
            return false;
        }
        return true;
    }

    public JButton getEnterInput() {
        return enterInput;
    }

    public JTextField getBlockNumberInput() {
        return blockNumberInput;
    }

    public void setBlockNumberInput(JTextField blockNumberInput) {
        this.blockNumberInput = blockNumberInput;
    }

    public JScrollPane getBlockScroll() {
        return blockScroll;
    }

    public void setBlockScroll(JScrollPane blockScroll) {
        this.blockScroll = blockScroll;
    }

    public JPanel getInternalMemory() {
        return internalMemory;
    }

    public void setInternalMemory(JPanel internalMemory) {
        this.internalMemory = internalMemory;
    }
}
