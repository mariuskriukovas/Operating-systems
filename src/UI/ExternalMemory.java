package UI;

import OS.RM.CPU;
import OS.Tools.Word;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import static java.lang.Integer.parseInt;
import static java.util.Arrays.copyOfRange;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

public class ExternalMemory {
    private JPanel externalMemory;
    private JTable block;
    private JButton enterInput;
    private JTextField blockInputArea;
    private JScrollPane blockScroll;
    private JLabel blockText;
    private CPU cpu;

    public ExternalMemory(CPU cpu) {
        this.cpu = cpu;

        enterInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String input = blockInputArea.getText();
                Integer blockNumber = parseInt(input);
                if(isNumeric(input) && blockNumber < 65536 && blockNumber >= 0) {
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
        Word[] content = cpu.getExternalMemory().getBlock(blockNumber);
        List<String> values = stream(content).map(Word::getHEXFormat).collect(toList());
        Object[] columnNames = new Object[6];
        String[][] tableOfValues = new String[256][6];
        for (int i = 1; i < 7; i++) {
            columnNames[i - 1] = (Integer) i;
        }
        for (int i = 0; i < values.size(); i++) {
            String value = values.get(i);
            String[] word = value.split("").clone();
            tableOfValues[i] = copyOfRange(word, 0, 6);
        }
        DefaultTableModel model = new DefaultTableModel(tableOfValues, columnNames);
        System.out.println(tableOfValues.toString());
        System.out.println(columnNames.toString());
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

    public JPanel getExternalMemory() {
        return externalMemory;
    }

    public void setExternalMemory(JPanel externalMemory) {
        this.externalMemory = externalMemory;
    }
}
