package jedit;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImageFilter;
import java.awt.print.PrinterException;
import java.io.*;

import javax.swing.UIManager.*;
import javax.swing.filechooser.FileNameExtensionFilter;


public class JEdit extends JFrame implements ActionListener{
    JTextArea   textArea;
    JScrollPane scrollPane;
    File currentFile;
    String copiedText="";
    JEdit(){
        setBounds(0,0,640,480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLAF();
        initMenu();
        initTextArea();
        newFile();
    }
    public static void main(String[] args){
        new JEdit().setVisible(true);
    }
    @Override
    public void actionPerformed(ActionEvent ae){
        switch(ae.getActionCommand()){
            case "New":
                newFile();
                break;
            case "Open":
                openFile();
                break;
            case "Save":
                if (currentFile==null)
                    saveAs();
                else
                    saveFile();
                break;
            case "Save As":
                saveAs();
                break;
            case "Print":
                printFile();
                break;
            case "Cut":
                copiedText = textArea.getSelectedText();
                textArea.cut();
                break;
            case "Copy":
                textArea.copy();
                break;
            case "Paste":
                textArea.paste();
                break;
            case "Select All":
                textArea.selectAll();
                break;
            case "Word Wrap":
                toggleWordWrap();
                break;
            case "About":
                JOptionPane.showMessageDialog(null,
                        "jEdit v.1.0\n(C) Lorem Ipsum 2021",
                        "About jEdit ",
                        JOptionPane.INFORMATION_MESSAGE
                );
                break;
            case "Quit":
                System.exit(0);
                break;
        }
    }
    private void setLAF(){
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }
    }
    public void toggleWordWrap(){
        textArea.setLineWrap(!textArea.getLineWrap());
    }
    private void newFile(){
        setTitle("Untitled - jEdit");
        textArea.setText("");
    }

    private void openFile(){
        JFileChooser fOpen = new JFileChooser();
        fOpen.setAcceptAllFileFilterUsed(false);
        fOpen.addChoosableFileFilter(new FileNameExtensionFilter("Text Documents (*.txt)","txt"));
        if (fOpen.showOpenDialog(this)!=JFileChooser.APPROVE_OPTION)
            return;
        File file = new File(fOpen.getSelectedFile().toString());
        currentFile = file;

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            textArea.read(br,null);
            setTitle(file.getName()+" - jEdit");
        }catch(Exception e){}
    }

    private void printFile() {
        try{ textArea.print(); }  catch(Exception e){}
    }

    private void saveFile(){
        try{
            BufferedWriter outFile = new BufferedWriter(new FileWriter(currentFile));
            textArea.write(outFile);
            setTitle(currentFile.getName()+" - jEdit");
        }catch(Exception e){}
    }

    private void saveAs(){
        JFileChooser fSaveAs = new JFileChooser();
        fSaveAs.setDialogTitle("Save As");
        if (fSaveAs.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
        return;
        String pathname = fSaveAs.getSelectedFile().toString();
        File file = new File(removeTXT(pathname)+".txt");

        if(file.exists())
            if (JOptionPane.showConfirmDialog(this, file.getName() + " already exists\nDo you want to replace it?", "Confirm Save As",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION)
                saveAs();


        currentFile = file;
        saveFile();

    }
    private void initMenu(){
        JMenuBar menubar = new JMenuBar();

        JMenu mFile   = new JMenu("File");
        JMenu mEdit   = new JMenu("Edit");
        JMenu mFormat = new JMenu("Format");
        JMenu mHelp   = new JMenu("Help");

        menubar.add(mFile);
        menubar.add(mEdit);
        menubar.add(mFormat);
        menubar.add(mHelp);

        JMenuItem mNew    = new JMenuItem("New");
        JMenuItem mOpen   = new JMenuItem("Open");
        JMenuItem mSave   = new JMenuItem("Save");
        JMenuItem mSaveAs = new JMenuItem("Save As");
        JMenuItem mPrint  = new JMenuItem("Print");
        JMenuItem mExit   = new JMenuItem("Quit");

        mNew.setAccelerator    (KeyStroke.getKeyStroke (KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        mOpen.setAccelerator   (KeyStroke.getKeyStroke (KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        mSave.setAccelerator   (KeyStroke.getKeyStroke (KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        mSaveAs.setAccelerator (KeyStroke.getKeyStroke (KeyEvent.VK_S, ActionEvent.CTRL_MASK+ActionEvent.SHIFT_MASK));
        mPrint.setAccelerator  (KeyStroke.getKeyStroke (KeyEvent.VK_P, ActionEvent.CTRL_MASK));
        mExit.setAccelerator   (KeyStroke.getKeyStroke (KeyEvent.VK_Q, ActionEvent.CTRL_MASK));

        mFile.add(mNew);
        mFile.add(mOpen);
        mFile.add(mSave);
        mFile.add(mSaveAs);
        mFile.add(mPrint);
        mFile.add(mExit);

        JMenuItem mCut       = new JMenuItem("Cut");
        JMenuItem mCopy      = new JMenuItem("Copy");
        JMenuItem mPaste     = new JMenuItem("Paste");
        JMenuItem mReplace   = new JMenuItem("Replace");
        JMenuItem mSelectAll = new JMenuItem("Select All");

        mCut.setAccelerator        (KeyStroke.getKeyStroke (KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        mCopy.setAccelerator       (KeyStroke.getKeyStroke (KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        mPaste.setAccelerator      (KeyStroke.getKeyStroke (KeyEvent.VK_V, ActionEvent.CTRL_MASK));
        mReplace.setAccelerator    (KeyStroke.getKeyStroke (KeyEvent.VK_R, ActionEvent.CTRL_MASK));
        mSelectAll.setAccelerator  (KeyStroke.getKeyStroke (KeyEvent.VK_A, ActionEvent.CTRL_MASK));

        mEdit.add(mCut);
        mEdit.add(mCopy);
        mEdit.add(mPaste);
        mEdit.add(mReplace);
        mEdit.add(mSelectAll);

        JMenuItem mWordWrap = new JCheckBoxMenuItem("Word Wrap");
        mFormat.add(mWordWrap);

        JMenuItem mAbout = new JMenuItem("About");
        mHelp.add(mAbout);

        mNew.addActionListener       (this);
        mOpen.addActionListener      (this);
        mSave.addActionListener      (this);
        mSaveAs.addActionListener    (this);
        mPrint.addActionListener     (this);
        mExit.addActionListener      (this);
        mCut.addActionListener       (this);
        mCopy.addActionListener      (this);
        mPaste.addActionListener     (this);
        mReplace.addActionListener   (this);
        mSelectAll.addActionListener (this);
        mWordWrap.addActionListener  (this);
        mAbout.addActionListener     (this);

        setJMenuBar(menubar);
    }
    private void initTextArea(){
        textArea = new JTextArea();
        textArea.setFont(new Font("SAN_SERIF", Font.PLAIN, 20));
        scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);
    }
    // remove txt extension from filename
    private String removeTXT(String f){
        if (f.length()<5) return f;
        if(f.substring(f.length()-4,f.length()).toLowerCase().equals(".txt"))
            return f.substring(0,f.length()-4);
        else
            return f;
    }
}
