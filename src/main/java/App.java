package main.java;

import javax.swing.* ;
import java.awt.* ;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.rmi.server.ExportException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatInspector;
import com.formdev.flatlaf.intellijthemes.FlatArcOrangeIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatCyanLightIJTheme;
import main.java.client.MyClient;
import main.java.client.OBECourse;
import main.java.client.OBEManager;
import main.java.server.Crawler;
import main.java.ui.* ;
import main.java.ui.mycompo.progressBarFrame;
import net.miginfocom.swing.MigLayout;

public class App {
    public static boolean isForum = false;
    public static boolean islogin = false ;
    public static boolean isFinishedUpdate = false;
    public static String username ;
    public static String password ;
    public static JFrame frame ;
    public static JPanel mainPanelCenter , mainToolPanel ;

    public static ToolbarPanel toolbarPanel ;
    public static HomePanel homePanel ;
    public static ClassesPanel classesPanel ;
    public static SettingsPanel settingsPanel ;
    public static ForumPanel forumPanel;
    public static DownloadPanel downloadPanel ;
    public static SearchPanel searchPanel;

    public static OBEManager student;
    public static MyClient myclient;
    public static ArrayList<Map<String, String>> update = new ArrayList<>();

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
        public progressBarFrame pbar = null ;

        public Login() {
            setTitle("登录");
            setModal( true );
            setSize(350, 250);
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
                jtaid.putClientProperty( "JComponent.roundRect", true );
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
                jtapw.putClientProperty( "JComponent.roundRect", true );
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
                jbok.putClientProperty( "JButton.buttonType", "roundRect" );
                JButton jbcancle = new JButton("取 消");
                jbcancle.putClientProperty( "JButton.buttonType", "roundRect" );

                jbok.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.out.println( "????" ) ;
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                jbok.setEnabled(false);
                            }
                        });
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                username = jtaid.getText();
                                password = jtapw.getText();
                                student.setInfo(username, password) ;

                                islogin = student.doLogin();
                                if( islogin ) {
                                    pbar = new progressBarFrame("登录中");
                                    pbar.showIt();
                                    pbar.setNowHint("正在连接OBE服务器...");
                                    student.initsDocument();
                                    int maxc = student.getTotCourse() ;
                                    pbar.init( maxc ) ;
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            while( true ){
                                                int nowc = student.getCourses().size() ;
                                                pbar.setVal( nowc ) ;
                                                pbar.setNowHint( "正在加载：" + student.getNowCrawlingCourse() ); ;
                                                try {
                                                    Thread.sleep( 200 ) ;
                                                } catch ( Exception ae ) { ae.printStackTrace(); }
                                                if( nowc >= maxc ){
                                                    pbar.deleteIt();
                                                };
                                            }
                                        }
                                    }).start();
                                    student.getContent();
                                    student.createDataFolders("");
                                }
                                try {
                                    SwingUtilities.invokeAndWait(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (islogin == true) {
                                                setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
                                                pbar.deleteIt();
                                                dispose();
                                            }
                                            else {
                                                JOptionPane.showMessageDialog(null, "账号或者密码错误！", "登录失败", JOptionPane.ERROR_MESSAGE);
                                                jbok.setEnabled(true);
                                            }
                                        }
                                    });
                                } catch ( Exception ae ){ ae.printStackTrace(); System.exit(1);}
                            }
                        }).start() ;
                    }
                });
                jbcancle.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.exit(0);
                    }
                });

                addWindowListener(new WindowListener() {
                    @Override public void windowOpened(WindowEvent e) {}
                    @Override public void windowClosing(WindowEvent e) { System.out.println("exit!"); System.exit( 0 ) ; }
                    @Override public void windowClosed(WindowEvent e) { }
                    @Override public void windowIconified(WindowEvent e) { }
                    @Override public void windowDeiconified(WindowEvent e) { }
                    @Override public void windowActivated(WindowEvent e) { }
                    @Override public void windowDeactivated(WindowEvent e) { }
                });

                panelButton.add(jbok, "cell 0 0");
                panelButton.add(jbcancle, "cell 1 0");
                add(panelButton, "cell 0 3,align center,growx 0");
            }
        }

        public void login() {
            this.setVisible(true);
        }
    }

    App() throws InterruptedException {
        student = new OBEManager();
        initialize() ;
        Login fLogin =  new Login() ;
        fLogin.login();

        if (islogin == false) System.exit(0);
        myclient = new MyClient("username");
        writeLoginTimeLog() ;
        while (!isFinishedUpdate) {
            try {
                Thread.sleep(100);
            }
            catch (Exception e) { }
        }
        addComponent() ;
    }

    private void initialize(){
        try {
            Calendar cal = new GregorianCalendar() ;
            int hour = cal.get( Calendar.HOUR_OF_DAY ) ;
            if(hour <= 6 || hour >= 18) {
                UIManager.setLookAndFeel( new FlatDarculaLaf() ) ;
            }
            else {
                UIManager.setLookAndFeel( new FlatArcOrangeIJTheme() ) ;
            }
            UIManager.put( "TitlePane.unifiedBackground", true );
            UIManager.put( "TitlePane.menuBarEmbedded", true );
            UIManager.put( "ScrollBar.width", 10 );
            UIManager.put( "ScrollBar.thumbArc", 999 );
            UIManager.put( "ScrollBar.thumbInsets", new Insets( 2, 2, 2, 2 ) );
            UIManager.put( "TabbedPane.tabAlignment" , "leading" ) ;
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
        forumPanel.initialize();
        settingsPanel = new SettingsPanel() ;
        downloadPanel = new DownloadPanel() ;
        searchPanel = new SearchPanel();

        mainToolPanel.add( toolbarPanel , BorderLayout.CENTER ) ;
        mainPanelCenter.add( homePanel , BorderLayout.CENTER ) ;

        frame.add( mainPanel ) ;
        mainPanel.updateUI();

    }

    void writeLoginTimeLog(){
        try {
            String ti;
            File configFile = new File("config.txt");
            if(!configFile.exists()) {
                // set default
                ti = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()).toString();
            } else {
                var fin = new BufferedReader(new FileReader("config.txt"));
                ti = fin.readLine();
            }
            System.err.println("time = " + ti);
            ArrayList<OBECourse> cl = student.getCourses();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Task", "queryUPDATE");
            jsonObject.put("Time", ti);
            jsonObject.put("size", Integer.toString(cl.size()));
            int sum = 0;
            for (OBECourse c : cl) {
                jsonObject.put(Integer.toString(sum), c.getCourseID());
                sum += 1;
            }
            myclient.send(jsonObject);
        }
        catch (Exception e) { }
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        var osw = new OutputStreamWriter(new FileOutputStream("./config.txt"), "UTF-8");
                        osw.write(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
                        osw.close();
                        Thread.sleep(30 * 1000);
                    }
                    catch (Exception e) { }
                }
            }
        }).start();
    }
}
