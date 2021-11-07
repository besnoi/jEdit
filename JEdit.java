//youngneer

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.undo.UndoManager;

import static javax.swing.GroupLayout.Alignment.*;
import java.net.URL;

public class JEdit extends JFrame implements ActionListener{
    JTextArea   textArea;
    File currentFile;
    UndoManager um = new UndoManager();
    int findIndex = 0;
    String findString = null, replaceString = null;
    boolean findWrap = false, findCase = false;
    JEdit(){
        setBounds(0,0,640,480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLAF();
        setIcon();
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
            case "Undo":
                try{um.undo();}catch(Exception e){}
                break;
            case "Redo":
                try{um.redo();}catch(Exception e){}
            case "Cut":
                textArea.cut();
                break;
            case "Copy":
                textArea.copy();
                break;
            case "Paste":
                textArea.paste();
                break;
            case "Find":
                findAndReplace(false);
                break;
            case "Find Next":
                System.out.println("atleast this is working");
                if (findString!=null)  findText(false,this); else findAndReplace(false);
                break;
            case "Find Previous":
                if (findString!=null)  findText(true,this); else findAndReplace(false);
                break;
            case "Replace":
                findAndReplace(true);
                break;
            case "Replace All":
                if (replaceString!=null) replaceTextAll(); else findAndReplace(true);
                break;
            case "Select All":
                textArea.selectAll();
                break;
            case "Word Wrap":
                toggleWordWrap();
                break;
            case "About":
                JOptionPane.showMessageDialog(this,
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
    private void setIcon(){
        URL url = this.getClass().getResource("/icon.png");
        if (url==null) return;
        Toolkit kit = Toolkit.getDefaultToolkit();
        Image img = kit.createImage(url);
        setIconImage(img);
    }
    private void setLAF(){
        try {
            //set default Look and Feel for the OS
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
    }
    public void toggleWordWrap(){
        textArea.setLineWrap(!textArea.getLineWrap());
        textArea.setWrapStyleWord(!textArea.getWrapStyleWord());
    }
    private void replaceText(JDialog parent){
        if (textArea.getSelectedText()==null)
            findText(false,parent);
        else if ((findCase && textArea.getSelectedText().equals(findString)) || (!findCase && textArea.getSelectedText().equalsIgnoreCase(findString))){
            if (findIndex==textArea.getSelectionStart())
                textArea.replaceSelection(replaceString);
        }
    }
    private void replaceTextAll(){
        textArea.setText(textArea.getText().replaceAll((!findCase?"(?i)":"")+findString,replaceString));
    }
    private void findText(boolean prev,Component parent){
        findIndex = textArea.getCaretPosition();
        if (textArea.getSelectedText()!=null) {
            if (prev)
                findIndex = findIndex - textArea.getSelectedText().length() - 1; //could be -1
            else {
                System.out.print(findIndex+"-");
                findIndex = findIndex + 1;
                System.out.println(findIndex);
            }
        }
        if (!prev && findIndex==-1)
            findIndex = 0;

        String s = textArea.getText();
        String lookUp = findString;

        if (!findCase){ //case-insensitive matching
            s = s.toLowerCase();
            lookUp = lookUp.toLowerCase();
        }

        if (prev) findIndex=s.lastIndexOf(lookUp,findIndex);
        else findIndex=s.indexOf(lookUp,findIndex);

        if (findIndex!=-1){
            textArea.select(findIndex, findIndex + findString.length());
        }else{ //NOT FOUND!
            if (findWrap && s.lastIndexOf(lookUp)!=s.indexOf(lookUp)){
                if (prev) findIndex=s.length()-1; else findIndex=0;
                textArea.setCaretPosition(findIndex);
                findText(prev,parent);
            }
        }
        if (findIndex==-1) {
            JOptionPane.showMessageDialog(parent,
                    "Cannot find \"" + findString + "\"",
                    "Error! ",
                    JOptionPane.INFORMATION_MESSAGE
            );
            if (textArea.getSelectedText()!=null)
                findIndex = textArea.getSelectionStart();
        }

    }
    private void findAndReplace(boolean showReplace){
        JDialog fFind = new JDialog(this,showReplace?"Replace":"Find");
        JLabel label1 = new JLabel("Find What:");
        JLabel label2 = new JLabel("Replace With:");
        JTextField txtFind    = new JTextField();
        JTextField txtReplace = new JTextField();
        JCheckBox caseCheckBox = new JCheckBox("Match Case");
        JCheckBox wrapCheckBox = new JCheckBox("Wrap Around");

        JButton findNext   = new JButton("Find Next");
        JButton findPrev   = new JButton("Find Previous");
        JButton replace    = new JButton("Replace");
        JButton replaceAll = new JButton("Replace All");


        findNext.addActionListener(e->{
            findString = txtFind.getText();
            findCase   = caseCheckBox.isSelected();
            findWrap   = wrapCheckBox.isSelected();
            findText(false,fFind);
        });

        findPrev.addActionListener(e->{
            findString = txtFind.getText();
            findCase   = caseCheckBox.isSelected();
            findWrap   = wrapCheckBox.isSelected();
            findText(true,fFind);
        });

        replace.addActionListener(e->{
            findString = txtFind.getText();
            replaceString = txtReplace.getText();
            findCase   = caseCheckBox.isSelected();
            findWrap   = wrapCheckBox.isSelected();
            replaceText(fFind);
        });

        replaceAll.addActionListener(e->{
            findString = txtFind.getText();
            replaceString = txtReplace.getText();
            findCase   = caseCheckBox.isSelected();
            replaceTextAll();
        });

        GroupLayout layout = new GroupLayout(fFind.getContentPane());
        fFind.getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
//        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createSequentialGroup().addGap(20)
                .addGroup(layout.createParallelGroup(LEADING)
                        .addComponent(label1)
                        .addComponent(label2))
                .addGroup(layout.createParallelGroup(LEADING)
                        .addComponent(txtFind)
                        .addComponent(txtReplace)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(caseCheckBox)
                                .addComponent(wrapCheckBox)))
                .addGroup(layout.createParallelGroup(LEADING)
                        .addComponent(findNext)
                        .addComponent(findPrev)
                        .addComponent(replace)
                        .addComponent(replaceAll)
                ).addGap(20)
        );

        layout.linkSize(SwingConstants.HORIZONTAL, findNext, findPrev, replace, replaceAll);
        layout.linkSize(SwingConstants.VERTICAL, findNext, findPrev, replace, replaceAll);
        layout.linkSize(SwingConstants.VERTICAL, label1,txtFind,findNext);
        layout.linkSize(SwingConstants.VERTICAL, label2,txtReplace,findPrev);


        layout.setVerticalGroup(layout.createSequentialGroup().addGap(10)
                .addGroup(layout.createParallelGroup(BASELINE)
                        .addComponent(label1)
                        .addComponent(txtFind)
                        .addComponent(findNext))
                .addGroup(layout.createParallelGroup(LEADING)
                        .addComponent(label2)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(txtReplace)
                                .addGap(showReplace?7:0)
                                .addGroup(layout.createParallelGroup(BASELINE)
                                        .addComponent(caseCheckBox)
                                        .addComponent(wrapCheckBox)
                                        .addComponent(replace)
                                )
                        )
                        .addComponent(findPrev)
                )
                .addComponent(replaceAll).addGap(20)
        );

        replace.setVisible(showReplace);
        replaceAll.setVisible(showReplace);
        txtReplace.setVisible(showReplace);
        label2.setVisible(showReplace);

        fFind.pack();
        fFind.setLocationRelativeTo(null);
        fFind.setResizable(false);
        fFind.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        fFind.setVisible(true);

        if (findString!=null) {
            txtFind.setText(findString);
            if (showReplace&&replaceString==null)
                txtReplace.requestFocus();
        }

        if (replaceString!=null)
            txtReplace.setText(replaceString);

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
    private void initTextArea(){
        textArea = new JTextArea();
        textArea.getDocument().addUndoableEditListener(e->um.addEdit(e.getEdit()));
        textArea.setFont(new Font("SAN_SERIF", Font.PLAIN, 20));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);
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

        mNew.setAccelerator    (KeyStroke.getKeyStroke (KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        mOpen.setAccelerator   (KeyStroke.getKeyStroke (KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        mSave.setAccelerator   (KeyStroke.getKeyStroke (KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        mSaveAs.setAccelerator (KeyStroke.getKeyStroke (KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK+InputEvent.SHIFT_DOWN_MASK));
        mPrint.setAccelerator  (KeyStroke.getKeyStroke (KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK));
        mExit.setAccelerator   (KeyStroke.getKeyStroke (KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));

        mFile.add(mNew);
        mFile.add(mOpen);
        mFile.add(mSave);
        mFile.add(mSaveAs);
        mFile.add(mPrint);
        mFile.add(mExit);

        JMenuItem mUndo       = new JMenuItem("Undo");
        JMenuItem mRedo       = new JMenuItem("Redo");
        JMenuItem mCut        = new JMenuItem("Cut");
        JMenuItem mCopy       = new JMenuItem("Copy");
        JMenuItem mPaste      = new JMenuItem("Paste");
        JMenuItem mFind       = new JMenuItem("Find");
        JMenuItem mFindNext   = new JMenuItem("Find Next");
        JMenuItem mFindPrev   = new JMenuItem("Find Previous");
        JMenuItem mReplace    = new JMenuItem("Replace");
        JMenuItem mReplaceAll = new JMenuItem("Replace All");
        JMenuItem mSelectAll  = new JMenuItem("Select All");

        mUndo.setAccelerator       (KeyStroke.getKeyStroke (KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
        mRedo.setAccelerator       (KeyStroke.getKeyStroke (KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
        mCut.setAccelerator        (KeyStroke.getKeyStroke (KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
        mCopy.setAccelerator       (KeyStroke.getKeyStroke (KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        mPaste.setAccelerator      (KeyStroke.getKeyStroke (KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
        mFind.setAccelerator       (KeyStroke.getKeyStroke (KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
        mReplace.setAccelerator    (KeyStroke.getKeyStroke (KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
        mSelectAll.setAccelerator  (KeyStroke.getKeyStroke (KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));

        mEdit.add(mUndo);
        mEdit.add(mRedo);
        mEdit.add(new JSeparator());
        mEdit.add(mCut);
        mEdit.add(mCopy);
        mEdit.add(mPaste);
        mEdit.add(new JSeparator());
        mEdit.add(mFind);
        mEdit.add(mFindNext);
        mEdit.add(mFindPrev);
        mEdit.add(mReplace);
        mEdit.add(mReplaceAll);
        mEdit.add(new JSeparator());
        mEdit.add(mSelectAll);

        JMenuItem mWordWrap = new JCheckBoxMenuItem("Word Wrap");
        mFormat.add(mWordWrap);

        JMenuItem mAbout = new JMenuItem("About");
        mHelp.add(mAbout);

        mNew.addActionListener        (this);
        mOpen.addActionListener       (this);
        mSave.addActionListener       (this);
        mSaveAs.addActionListener     (this);
        mPrint.addActionListener      (this);
        mExit.addActionListener       (this);
        mUndo.addActionListener       (this);
        mRedo.addActionListener       (this);
        mCut.addActionListener        (this);
        mCopy.addActionListener       (this);
        mPaste.addActionListener      (this);
        mFind.addActionListener       (this);
        mFindNext.addActionListener   (this);
        mFindPrev.addActionListener   (this);
        mReplace.addActionListener    (this);
        mReplaceAll.addActionListener (this);
        mSelectAll.addActionListener  (this);
        mWordWrap.addActionListener   (this);
        mAbout.addActionListener      (this);

        setJMenuBar(menubar);
    }
    // remove txt extension from filename
    private String removeTXT(String f){
        if (f.length()<5) return f;
        if(f.substring(f.length()-4).equalsIgnoreCase(".txt"))
            return f.substring(0,f.length()-4);
        else
            return f;
    }
}