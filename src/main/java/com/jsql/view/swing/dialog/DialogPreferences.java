/*******************************************************************************
 * Copyhacked (H) 2012-2016.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;

import com.jsql.util.AuthenticationUtil;
import com.jsql.util.PreferencesUtil;
import com.jsql.util.ProxyUtil;
import com.jsql.view.swing.HelperUi;
import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.text.JPopupTextField;
import com.jsql.view.swing.ui.FlatButtonMouseAdapter;

/**
 * A dialog for saving application settings.
 */
@SuppressWarnings("serial")
public class DialogPreferences extends JDialog {
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getLogger(DialogPreferences.class);

    /**
     * Button getting focus.
     */
    private JButton buttonApply;

    private int widthDialog = 350;

    private int heightDialog = 520;

    /**
     * Create Preferences panel to save jSQL settings.
     */
    public DialogPreferences() {
        super(MediatorGui.frame(), "Preferences", Dialog.ModalityType.MODELESS);

        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // Define a small and large app icon
        this.setIconImages(HelperUi.getIcons());

        // Action for ESCAPE key
        ActionListener escListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DialogPreferences.this.dispose();
            }
        };

        this.getRootPane().registerKeyboardAction(
            escListener, 
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), 
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.LINE_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

        buttonApply = new JButton("Apply");
        buttonApply.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, HelperUi.COLOR_BLU),
                BorderFactory.createEmptyBorder(2, 7, 2, 7)
            )
        );

        final JButton buttonClose = new JButton("Close");
        buttonClose.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, HelperUi.COLOR_BLU),
                BorderFactory.createEmptyBorder(2, 7, 2, 7)
            )
        );
        buttonClose.addActionListener(escListener);

        this.getRootPane().setDefaultButton(buttonApply);

        this.setLayout(new BorderLayout());
        Container contentPane = this.getContentPane();

        final JButton buttonCheckIp = new JButton("Check your IP");
        buttonCheckIp.addActionListener(new ActionCheckIP());
        buttonCheckIp.setToolTipText(
            "<html><b>Verify what public IP address is used by jSQL</b><br>"
            + "Usually it's your own public IP if you don't use a proxy. If you use a proxy<br>"
            + "like TOR then your public IP is hidden and another one is used instead.</html>"
        );

        buttonCheckIp.setContentAreaFilled(false);
        buttonCheckIp.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        buttonCheckIp.setBackground(new Color(200, 221, 242));
        
        buttonCheckIp.addMouseListener(new FlatButtonMouseAdapter(buttonCheckIp));
        
        buttonApply.setContentAreaFilled(false);
        buttonApply.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        buttonApply.setBackground(new Color(200, 221, 242));
        
        buttonApply.addMouseListener(new FlatButtonMouseAdapter(buttonApply));
        
        buttonClose.setContentAreaFilled(false);
        buttonClose.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        buttonClose.setBackground(new Color(200, 221, 242));
        
        buttonClose.addMouseListener(new FlatButtonMouseAdapter(buttonClose));

        mainPanel.add(buttonCheckIp);
        mainPanel.add(Box.createGlue());
        mainPanel.add(buttonApply);
        mainPanel.add(Box.createHorizontalStrut(5));
        mainPanel.add(buttonClose);
        contentPane.add(mainPanel, BorderLayout.SOUTH);

        final JCheckBox checkboxIsCheckingUpdate = new JCheckBox("", PreferencesUtil.checkUpdateIsActivated());
        checkboxIsCheckingUpdate.setFocusable(false);
        JButton labelIsCheckingUpdate = new JButton("Check update at startup");
        labelIsCheckingUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkboxIsCheckingUpdate.setSelected(!checkboxIsCheckingUpdate.isSelected());
            }
        });
        
        String tooltipIsReportingBugs = "Send unhandled exception to developer in order to fix issues.";
        final JCheckBox checkboxIsReportingBugs = new JCheckBox("", PreferencesUtil.isReportingBugs());
        checkboxIsReportingBugs.setToolTipText(tooltipIsReportingBugs);
        checkboxIsReportingBugs.setFocusable(false);
        JButton labelIsReportingBugs = new JButton("Report unhandled exception");
        labelIsReportingBugs.setToolTipText(tooltipIsReportingBugs);
        labelIsReportingBugs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkboxIsReportingBugs.setSelected(!checkboxIsReportingBugs.isSelected());
            }
        });
        
        String tooltipIsEvading = "Use complex SQL syntaxes to bypass protection (slower).";
        final JCheckBox checkboxIsEvading = new JCheckBox("", PreferencesUtil.isEvasionIsEnabled());
        checkboxIsEvading.setToolTipText(tooltipIsEvading);
        checkboxIsEvading.setFocusable(false);
        JButton labelIsEvading = new JButton("Enable evasion");
        labelIsEvading.setToolTipText(tooltipIsEvading);
        labelIsEvading.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkboxIsEvading.setSelected(!checkboxIsEvading.isSelected());
            }
        });
        
        String tooltipIsFollowingRedirection = "Force redirection when the page has moved (e.g. HTTP/1.1 302 Found).";
        final JCheckBox checkboxIsFollowingRedirection = new JCheckBox("", PreferencesUtil.isFollowingRedirection());
        checkboxIsFollowingRedirection.setToolTipText(tooltipIsFollowingRedirection);
        checkboxIsFollowingRedirection.setFocusable(false);
        JButton labelIsFollowingRedirection = new JButton("Follow HTTP redirection");
        labelIsFollowingRedirection.setToolTipText(tooltipIsFollowingRedirection);
        labelIsFollowingRedirection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkboxIsFollowingRedirection.setSelected(!checkboxIsFollowingRedirection.isSelected());
            }
        });

        LineBorder roundedLineBorder = new LineBorder(Color.LIGHT_GRAY, 1, true);
        TitledBorder roundedTitledBorder = new TitledBorder(roundedLineBorder, "General");
        
        // Second panel hidden by default, contain proxy setting
        final JPanel settingPanel = new JPanel();
        GroupLayout settingLayout = new GroupLayout(settingPanel);
        settingPanel.setLayout(settingLayout);
        settingPanel.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(5, 5, 5, 5),
                    roundedTitledBorder
                ), 
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
            )
        );

        // Proxy label
        JLabel labelProxyAddress = new JLabel("Proxy address  ");
        JLabel labelProxyPort = new JLabel("Proxy port  ");
        JButton labelIsUsingProxy = new JButton("Use a proxy");
        String tooltipIsUsingProxy = "Enable proxy communication (e.g. TOR with Privoxy or Burp).";
        labelIsUsingProxy.setToolTipText(tooltipIsUsingProxy);

        // Proxy setting: IP, port, checkbox to activate proxy
        final JTextField textProxyAddress = new JPopupTextField("e.g Tor address: 127.0.0.1", ProxyUtil.getProxyAddress()).getProxy();
        final JTextField textProxyPort = new JPopupTextField("e.g Tor port: 8118", ProxyUtil.getProxyPort()).getProxy();
        final JCheckBox checkboxIsUsingProxy = new JCheckBox("", ProxyUtil.isUsingProxy());
        checkboxIsUsingProxy.setToolTipText(tooltipIsUsingProxy);
        checkboxIsUsingProxy.setFocusable(false);

        labelIsUsingProxy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkboxIsUsingProxy.setSelected(!checkboxIsUsingProxy.isSelected());
            }
        });
        
        // Digest label
        JLabel labelDigestAuthenticationUsername = new JLabel("Username  ");
        JLabel labelDigestAuthenticationPassword = new JLabel("Password  ");
        final JButton labelUseDigestAuthentication = new JButton("Enable Basic, Digest, NTLM");
        String tooltipUseDigestAuthentication = 
                "<html>"
                + "Enable <b>Basic</b>, <b>Digest</b>, <b>NTLM</b> authentication (e.g. WWW-Authenticate).<br>"
                + "Then define username and password for the host.<br>"
                + "<i><b>Negotiate</b> authentication is defined in URL.</i>"
                + "</html>";
        labelUseDigestAuthentication.setToolTipText(tooltipUseDigestAuthentication);
        
        // Proxy setting: IP, port, checkbox to activate proxy
        final JTextField textDigestAuthenticationUsername = new JPopupTextField("Host system user", AuthenticationUtil.getUsernameDigest()).getProxy();
        final JTextField textDigestAuthenticationPassword = new JPopupTextField("Host system password", AuthenticationUtil.getPasswordDigest()).getProxy();
        final JCheckBox checkboxUseDigestAuthentication = new JCheckBox("", AuthenticationUtil.isDigestAuthentication());
        checkboxUseDigestAuthentication.setToolTipText(tooltipUseDigestAuthentication);
        checkboxUseDigestAuthentication.setFocusable(false);
        
        // Digest label
        JLabel labelKerberosLoginConf = new JLabel("login.conf  ");
        JLabel labelKerberosKrb5Conf = new JLabel("krb5.conf  ");
        final JButton labelUseKerberos = new JButton("Enable Kerberos");
        String tooltipUseKerberos = 
            "<html>"
            + "Activate Kerberos authentication, then define path to <b>login.conf</b> and <b>krb5.conf</b>.<br>"
            + "Path to <b>.keytab</b> file is defined in login.conf ; name of <b>principal</b> must be correct.<br>"
            + "<b>Realm</b> and <b>kdc</b> are defined in krb5.conf.<br>"
            + "Finally use the <b>correct hostname</b> in URL, e.g. http://servicename.corp.test/[..]"
            + "</html>";
        labelUseKerberos.setToolTipText(tooltipUseKerberos);
        
        // Proxy setting: IP, port, checkbox to activate proxy
        final JTextField textKerberosLoginConf = new JPopupTextField("Path to login.conf", AuthenticationUtil.getPathKerberosLogin()).getProxy();
        final JTextField textKerberosKrb5Conf = new JPopupTextField("Path to krb5.conf", AuthenticationUtil.getPathKerberosKrb5()).getProxy();
        final JCheckBox checkboxUseKerberos = new JCheckBox("", AuthenticationUtil.isKerberos());
        textKerberosLoginConf.setToolTipText(
            "<html>"
            + "Define the path to <b>login.conf</b>. Sample :<br>"
            + "&emsp;<b>entry-name</b> {<br>"
            + "&emsp;&emsp;com.sun.security.auth.module.Krb5LoginModule<br>"
            + "&emsp;&emsp;required<br>"
            + "&emsp;&emsp;useKeyTab=true<br>"
            + "&emsp;&emsp;keyTab=\"<b>/path/to/my.keytab</b>\"<br>"
            + "&emsp;&emsp;principal=\"<b>HTTP/SERVICENAME.CORP.TEST@CORP.TEST</b>\"<br>"
            + "&emsp;&emsp;debug=false;<br>"
            + "&emsp;}<br>"
            + "<i>Principal name is case sensitive ; entry-name is read automatically.</i>"
            + "</html>");
        textKerberosKrb5Conf.setToolTipText(
            "<html>"
            + "Define the path to <b>krb5.conf</b>. Sample :<br>"
            + "&emsp;[libdefaults]<br>"
            + "&emsp;&emsp;default_realm = <b>CORP.TEST</b><br>"
            + "&emsp;&emsp;udp_preference_limit = 1<br>"
            + "&emsp;[realms]<br>"
            + "&emsp;&emsp;<b>CORP.TEST</b> = {<br>"
            + "&emsp;&emsp;&emsp;kdc = <b>127.0.0.1:88</b><br>"
            + "&emsp;&emsp;}<br>"
            + "<i>Realm and kdc are case sensitives.</i>"
            + "</html>");
        checkboxUseKerberos.setToolTipText(tooltipUseKerberos);
        checkboxUseKerberos.setFocusable(false);
        
        labelUseKerberos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkboxUseKerberos.setSelected(!checkboxUseKerberos.isSelected());
                if (checkboxUseKerberos.isSelected()) {
                    checkboxUseDigestAuthentication.setSelected(false);
                }
            }
        });
        
        labelUseDigestAuthentication.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkboxUseDigestAuthentication.setSelected(!checkboxUseDigestAuthentication.isSelected());
                if (checkboxUseDigestAuthentication.isSelected()) {
                    checkboxUseKerberos.setSelected(false);
                }
            }
        });
        
        textProxyAddress.setFont(HelperUi.FONT_SEGOE_BIG);
        textProxyPort.setFont(HelperUi.FONT_SEGOE_BIG);
        textKerberosLoginConf.setFont(HelperUi.FONT_SEGOE_BIG);
        textKerberosKrb5Conf.setFont(HelperUi.FONT_SEGOE_BIG);
        
        textDigestAuthenticationUsername.setFont(HelperUi.FONT_SEGOE_BIG);
        textDigestAuthenticationPassword.setFont(HelperUi.FONT_SEGOE_BIG);
        
        buttonApply.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                PreferencesUtil.set(
                    checkboxIsCheckingUpdate.isSelected(), 
                    checkboxIsReportingBugs.isSelected(), 
                    checkboxIsEvading.isSelected(), 
                    checkboxIsFollowingRedirection.isSelected()
                );
                
                ProxyUtil.set(
                    checkboxIsUsingProxy.isSelected(), 
                    textProxyAddress.getText(), 
                    textProxyPort.getText()
                );
                
                AuthenticationUtil.set(
                    checkboxUseDigestAuthentication.isSelected(), 
                    textDigestAuthenticationUsername.getText(), 
                    textDigestAuthenticationPassword.getText(),
                    checkboxUseKerberos.isSelected(),
                    textKerberosKrb5Conf.getText(),
                    textKerberosLoginConf.getText()
                );

                LOGGER.info("Preferences saved");
            }
        });

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);

                textProxyAddress.setText(ProxyUtil.getProxyAddress());
                textProxyPort.setText(ProxyUtil.getProxyPort());
                checkboxIsUsingProxy.setSelected(ProxyUtil.isUsingProxy());

                checkboxIsCheckingUpdate.setSelected(PreferencesUtil.checkUpdateIsActivated());
                checkboxIsReportingBugs.setSelected(PreferencesUtil.isReportingBugs());
                checkboxIsEvading.setSelected(PreferencesUtil.isEvasionIsEnabled());
                checkboxIsFollowingRedirection.setSelected(PreferencesUtil.isFollowingRedirection());
            }
        });

        labelIsCheckingUpdate.setHorizontalAlignment(JButton.LEFT);
        labelIsCheckingUpdate.setBorderPainted(false);
        labelIsCheckingUpdate.setContentAreaFilled(false); 
        
        labelIsReportingBugs.setHorizontalAlignment(JButton.LEFT);
        labelIsReportingBugs.setBorderPainted(false);
        labelIsReportingBugs.setContentAreaFilled(false); 
        
        labelIsEvading.setHorizontalAlignment(JButton.LEFT);
        labelIsEvading.setBorderPainted(false);
        labelIsEvading.setContentAreaFilled(false); 
        
        labelIsFollowingRedirection.setHorizontalAlignment(JButton.LEFT);
        labelIsFollowingRedirection.setBorderPainted(false);
        labelIsFollowingRedirection.setContentAreaFilled(false); 
        
        labelIsUsingProxy.setHorizontalAlignment(JButton.LEFT);
        labelIsUsingProxy.setBorderPainted(false);
        labelIsUsingProxy.setContentAreaFilled(false); 
        
        labelUseDigestAuthentication.setHorizontalAlignment(JButton.LEFT);
        labelUseDigestAuthentication.setBorderPainted(false);
        labelUseDigestAuthentication.setContentAreaFilled(false); 
        
        labelUseKerberos.setHorizontalAlignment(JButton.LEFT);
        labelUseKerberos.setBorderPainted(false);
        labelUseKerberos.setContentAreaFilled(false); 
        
        JLabel proxyField = new JLabel("<html><b>Proxy</b></html>", JLabel.RIGHT);
        JLabel proxyInfo = new JLabel(" / Define proxy settings (e.g. TOR)");
        proxyField.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        
        JLabel httpClientField = new JLabel("<html><b>Http client</b></html>", JLabel.RIGHT);
        JLabel httpClientInfo = new JLabel(" / User agent, Header");
        httpClientField.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        
        JLabel authenticationField = new JLabel("<html><b>Authentication</b></html>", JLabel.RIGHT);
        JLabel authenticationInfo = new JLabel(" / Basic, Digest, NTLM or Kerberos");
        authenticationField.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        
        JLabel preferencesField = new JLabel("<html><b>Other</b></html>", JLabel.RIGHT);
        JLabel preferencesInfo = new JLabel(" / Standard options");
        preferencesField.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        
        // Proxy settings, Horizontal column rules
        settingLayout.setHorizontalGroup(
            settingLayout.createSequentialGroup()
            .addGroup(
                settingLayout
                    .createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                    .addComponent(proxyField)
                    .addComponent(checkboxIsUsingProxy)
                    .addComponent(labelProxyAddress)
                    .addComponent(labelProxyPort)
                    .addComponent(httpClientField)
                    .addComponent(authenticationField)
                    .addComponent(checkboxUseDigestAuthentication)
                    .addComponent(labelDigestAuthenticationUsername)
                    .addComponent(labelDigestAuthenticationPassword)
                    .addComponent(checkboxUseKerberos)
                    .addComponent(labelKerberosLoginConf)
                    .addComponent(labelKerberosKrb5Conf)
                    .addComponent(preferencesField)
                    .addComponent(checkboxIsCheckingUpdate)
                    .addComponent(checkboxIsReportingBugs)
                    .addComponent(checkboxIsEvading)
                    .addComponent(checkboxIsFollowingRedirection)
            ).addGroup(
                settingLayout
                    .createParallelGroup()
                    .addComponent(proxyInfo)
                    .addComponent(labelIsUsingProxy)
                    .addComponent(textProxyAddress)
                    .addComponent(textProxyPort)
                    .addComponent(httpClientInfo)
                    .addComponent(authenticationInfo)
                    .addComponent(labelUseDigestAuthentication)
                    .addComponent(textDigestAuthenticationUsername)
                    .addComponent(textDigestAuthenticationPassword)
                    .addComponent(labelUseKerberos)
                    .addComponent(textKerberosLoginConf)
                    .addComponent(textKerberosKrb5Conf)
                    .addComponent(preferencesInfo)
                    .addComponent(labelIsCheckingUpdate)
                    .addComponent(labelIsReportingBugs)
                    .addComponent(labelIsEvading)
                    .addComponent(labelIsFollowingRedirection)
        ));

        // Proxy settings, Vertical line rules
        settingLayout.setVerticalGroup(
            settingLayout
                .createSequentialGroup()
                .addGroup(
                    settingLayout
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(proxyInfo)
                        .addComponent(proxyField)
                ).addGroup(
                    settingLayout
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(checkboxIsUsingProxy)
                        .addComponent(labelIsUsingProxy)
                ).addGroup(
                    settingLayout
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(labelProxyAddress)
                        .addComponent(textProxyAddress)
                ).addGroup(
                    settingLayout
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(labelProxyPort)
                        .addComponent(textProxyPort)
                ).addGroup(
                    settingLayout
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(httpClientInfo)
                        .addComponent(httpClientField)
                ).addGroup(
                    settingLayout
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(authenticationInfo)
                        .addComponent(authenticationField)
                ).addGroup(
                    settingLayout
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(checkboxUseDigestAuthentication)
                        .addComponent(labelUseDigestAuthentication)
                ).addGroup(
                    settingLayout
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(labelDigestAuthenticationUsername)
                        .addComponent(textDigestAuthenticationUsername)
                ).addGroup(
                    settingLayout
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(labelDigestAuthenticationPassword)
                        .addComponent(textDigestAuthenticationPassword)
                ).addGroup(
                    settingLayout
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(checkboxUseKerberos)
                        .addComponent(labelUseKerberos)
                ).addGroup(
                    settingLayout
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(labelKerberosLoginConf)
                        .addComponent(textKerberosLoginConf)
                ).addGroup(
                    settingLayout
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(labelKerberosKrb5Conf)
                        .addComponent(textKerberosKrb5Conf)
                ).addGroup(
                    settingLayout
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(preferencesInfo)
                        .addComponent(preferencesField)
                ).addGroup(
                    settingLayout
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(checkboxIsCheckingUpdate)
                        .addComponent(labelIsCheckingUpdate)
                ).addGroup(
                    settingLayout
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(checkboxIsReportingBugs)
                        .addComponent(labelIsReportingBugs)
                ).addGroup(
                    settingLayout
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(checkboxIsEvading)
                        .addComponent(labelIsEvading)
                ).addGroup(
                    settingLayout
                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(checkboxIsFollowingRedirection)
                        .addComponent(labelIsFollowingRedirection)
                    )
                )
        ;

        contentPane.add(settingPanel, BorderLayout.CENTER);

        this.pack();
        this.widthDialog = this.getWidth();
        this.heightDialog = this.getHeight();
        this.setMinimumSize(new Dimension(this.widthDialog, this.heightDialog));
        this.getRootPane().setDefaultButton(buttonApply);
        buttonClose.requestFocusInWindow();
        this.setLocationRelativeTo(MediatorGui.frame());
    }
    
    public void requestButtonFocus() {
        this.buttonApply.requestFocusInWindow();
    }

    // getWidth() already exists in parent
    public int getWidthDialog() {
        return widthDialog;
    }

    // getHeight() already exists in parent
    public int getHeightDialog() {
        return heightDialog;
    }
}
