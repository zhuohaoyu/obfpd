package main.java;

import javax.swing.* ;
import java.awt.* ;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.formdev.flatlaf.FlatDarculaLaf;
import main.java.client.OBEManager;
import main.java.ui.* ;
import net.miginfocom.swing.MigLayout;

public class App {
    public static boolean islogin ;
    public static String username ;
    public static String password ;
    public static JFrame frame ;
    public static JPanel mainPanelCenter , mainToolPanel ;

    public static TestPanel testPanel ;
    public static ToolbarPanel toolbarPanel ;
    public static HomePanel homePanel ;
    public static ClassesPanel classesPanel ;
    public static SettingsPanel settingsPanel ;
    public static ForumPanel forumPanel;

    public static OBEManager student;

    public static void main( String[] args ){
        EventQueue.invokeLater( ()->{
            try {
                App window = new App() ;
                window.frame.setVisible( true ) ;
            } catch ( Exception e ){
                e.printStackTrace() ;
            }
        } ) ;

    }

    public class Login extends JDialog {
        public JTextField jtaid = null;
        public JPasswordField  jtapw = null;

        public Login() {
            setTitle("登录");
            setModal(true);
            setSize(350, 250);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);

            setLayout(new MigLayout(
                    "insets 25,hidemode 3",
                    "[grow,fill]para",
                    "[]10[]10[]20[]"
            ));

            {
                JPanel panelTitle = new JPanel();
                JLabel label = new JLabel("登   录");
                label.setFont(UiConsts.FONT_TITLE2);
                panelTitle.add(label);
                add(panelTitle, "cell 0 0");
            }

            {
                JPanel panelID = new JPanel();
                panelID.setLayout(new MigLayout(
                        "insets 0,hidemode 3",
                        "[][grow,fill]para",
                        "[grow,fill]"
                ));
                JLabel label = new JLabel("学号：");
                jtaid = new JTextField();
                panelID.add(label, "cell 0 0");
                panelID.add(jtaid, "cell 1 0");
                add(panelID, "cell 0 1");
            }

            {
                JPanel panelPassword = new JPanel();
                panelPassword.setLayout(new MigLayout(
                        "insets 0,hidemode 3",
                        "[][grow,fill]para",
                        "[grow,fill]"
                ));
                JLabel label = new JLabel("密码：");
                jtapw = new JPasswordField ();
                panelPassword.add(label, "cell 0 0");
                panelPassword.add(jtapw, "cell 1 0");
                add(panelPassword, "cell 0 2");
            }

            {
                JPanel panelButton = new JPanel();
                panelButton.setLayout(new MigLayout(
                        "insets 0,hidemode 3",
                        "[]10[]",
                        "[grow,fill]"
                ));
                JButton jbok = new JButton("确 定");
                JButton jbcancle = new JButton("取 消");

                jbok.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        username = jtaid.getText();
                        password = jtapw.getText();
                        student.setInfo(username, password);
                        islogin = student.doLogin();
                        if (islogin == true) dispose();
                        else {
                            JOptionPane.showMessageDialog(null,"账号或者密码错误！","登录失败", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
                jbcancle.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.exit(0);
                    }
                });

                panelButton.add(jbok, "cell 0 0");
                panelButton.add(jbcancle, "cell 1 0");
                add(panelButton, "cell 0 3,align right,growx 0");
            }
        }

        public void login() {
            this.setVisible(true);
        }
    }

    App(){
        islogin = false;
        student = new OBEManager();
        initialize() ;
        new Login().login();
        if (islogin == false) System.exit(0);
        addComponent() ;
    }

    private void initialize(){
        try {
            UIManager.setLookAndFeel( new FlatDarculaLaf() ) ;
        } catch ( Exception e ){
            System.err.println( "set L&F failed" ) ;
            e.printStackTrace() ;
        }
        frame = new JFrame() ;
        frame.setBounds( UiConsts.MAIN_WINDOW_X,
                UiConsts.MAIN_WINDOW_Y,
                UiConsts.MAIN_WINDOW_WIDTH,
                UiConsts.MAIN_WINDOW_HEIGHT ) ;
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE ) ;
    }

    private void addComponent(){
        JPanel mainPanel = new JPanel( new BorderLayout() ) ;

        mainToolPanel = new JPanel( new BorderLayout() ) ;
        mainPanelCenter = new JPanel( new BorderLayout() ) ;
        mainPanel.add( mainToolPanel , BorderLayout.WEST ) ;
        mainPanel.add( mainPanelCenter , BorderLayout.CENTER ) ;

        toolbarPanel = new ToolbarPanel() ;
        homePanel = new HomePanel() ;
        classesPanel = new ClassesPanel() ;
        forumPanel = new ForumPanel();
        settingsPanel = new SettingsPanel() ;

        mainToolPanel.add( toolbarPanel , BorderLayout.CENTER ) ;
        mainPanelCenter.add( homePanel , BorderLayout.CENTER ) ;

        frame.add( mainPanel ) ;
        mainPanel.updateUI();

    }
}
