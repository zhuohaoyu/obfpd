package main.java.ui;


import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.ui.FlatButtonUI;
import com.formdev.flatlaf.ui.FlatRoundBorder;
import main.java.App;
import javax.swing.*;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.* ;
import java.awt.image.BufferedImage;

public class ToolbarPanel extends JPanel{
    private static JButton buttonHome ;
    private static JButton buttonClasses ;
    private static JButton buttonDownloadCenter ;
    private static JButton buttonForum;
    private static JButton buttonSettings ;

    public ToolbarPanel(){
        initialize() ;
        addButton() ;
        addListener() ;
    }

    private void initialize(){
        Dimension preferredSize = new Dimension( UiConsts.TOOLBAR_BUTTON_WIDTH , UiConsts.MAIN_WINDOW_HEIGHT ) ;
        this.setPreferredSize( preferredSize ) ;
        this.setMaximumSize( preferredSize ) ;
        this.setMinimumSize( preferredSize ) ;
        this.setLayout( new GridLayout( 2 , 1 ) ) ;
    }

    private ImageIcon getScaledImageIcon(String path, int w, int h){
        ImageIcon classIcon = new ImageIcon(path);
        Image srcImg = classIcon.getImage();
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();
        ImageIcon resizedClassIcon = new ImageIcon(resizedImg);
        return resizedClassIcon;
    }

    private void addButton(){
        JPanel panelUp = new JPanel( new FlowLayout( FlowLayout.CENTER ,  0 , 0 ) ) ;
        JPanel panelDown = new JPanel( new BorderLayout() ) ;

        Dimension buttonPreferredSize = new Dimension( UiConsts.TOOLBAR_BUTTON_WIDTH , UiConsts.TOOLBAR_BUTTON_HEIGHT ) ;

        buttonHome = new JButton() ;
        buttonHome.setPreferredSize( buttonPreferredSize ) ;
        buttonHome.setBorderPainted( false ) ;

        buttonClasses = new JButton() ;

        ImageIcon classIcon = getScaledImageIcon("./resources/homework.png", 48, 48);
        buttonClasses.setIcon(classIcon);
        buttonClasses.setPreferredSize( buttonPreferredSize ) ;
        buttonClasses.setBorderPainted( false );

        buttonDownloadCenter = new JButton() ;
        buttonDownloadCenter.setPreferredSize( buttonPreferredSize ) ;
        buttonDownloadCenter.setBorderPainted( false );

        buttonForum = new JButton() ;
        buttonForum.setPreferredSize( buttonPreferredSize ) ;
        buttonForum.setBorderPainted( false );

        panelUp.add( buttonHome ) ;
        panelUp.add( buttonClasses ) ;
        panelUp.add( buttonDownloadCenter ) ;
        panelUp.add( buttonForum );

        buttonSettings = new JButton() ;
        buttonSettings.setPreferredSize( buttonPreferredSize ) ;
        buttonSettings.setBorderPainted( false ) ;

        panelDown.add( buttonSettings , BorderLayout.SOUTH ) ;
        this.add( panelUp ) ;
        this.add( panelDown ) ;
    }

    private void addListener(){
        buttonHome.addActionListener( e -> {
            App.isForum = false;
            buttonHome.setSelected( true ) ;
            buttonClasses.setSelected( false ) ;
            buttonSettings.setSelected( false ) ;
            buttonForum.setSelected( false );
            App.mainPanelCenter.removeAll();
            App.homePanel.setContent() ;
            App.mainPanelCenter.add( App.homePanel , BorderLayout.CENTER );
            SwingUtilities.invokeLater(() -> App.mainPanelCenter.updateUI());
            FlatLaf.updateUI();
        } );
        buttonClasses.addActionListener( e -> {
            App.isForum = false;
            buttonHome.setSelected( false ) ;
            buttonClasses.setSelected( true ) ;
            buttonSettings.setSelected( false ) ;
            buttonForum.setSelected( false );
            App.mainPanelCenter.removeAll();
            App.classesPanel.setContent() ;
            App.mainPanelCenter.add( App.classesPanel , BorderLayout.CENTER );
            SwingUtilities.invokeLater(() -> App.mainPanelCenter.updateUI());
            FlatLaf.updateUI();
        } );

        buttonSettings.addActionListener( e-> {
            App.isForum = false;
            buttonHome.setSelected( false ) ;
            buttonClasses.setSelected( false ) ;
            buttonSettings.setSelected( true ) ;
            buttonForum.setSelected( false );
            App.mainPanelCenter.removeAll() ;
            App.mainPanelCenter.add( App.settingsPanel , BorderLayout.CENTER ) ;
            SwingUtilities.invokeLater(() -> App.mainPanelCenter.updateUI());
            FlatLaf.updateUI();
        });

        buttonForum.addActionListener( e-> {
            App.isForum = true;
            buttonHome.setSelected( false ) ;
            buttonClasses.setSelected( false ) ;
            buttonSettings.setSelected( false ) ;
            buttonForum.setSelected( true );
            App.mainPanelCenter.removeAll() ;
            App.forumPanel.initialize();
            App.mainPanelCenter.add( App.forumPanel , BorderLayout.CENTER ) ;
            SwingUtilities.invokeLater(() -> App.mainPanelCenter.updateUI());
            FlatLaf.updateUI();
        });
    }

    
}
