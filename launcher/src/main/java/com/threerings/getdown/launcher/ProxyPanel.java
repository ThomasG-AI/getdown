//
// Getdown - application installer, patcher and launcher
// Copyright (C) 2004-2018 Getdown authors
// https://github.com/threerings/getdown/blob/master/LICENSE

package com.threerings.getdown.launcher;

import static com.threerings.getdown.Log.log;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.samskivert.swing.GroupLayout;
import com.samskivert.swing.Spacer;
import com.samskivert.swing.VGroupLayout;
import com.threerings.getdown.util.MessageUtil;

/**
 * Displays an interface with which the user can configure their proxy
 * settings.
 */
public final class ProxyPanel extends JPanel implements ActionListener
{
  public ProxyPanel(Getdown getdown, ResourceBundle msgs)
  {
    _getdown = getdown;
    _msgs = msgs;

    setLayout(new VGroupLayout());
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    add(new SaneLabelField(get("m.configure_proxy")));
    add(new Spacer(5, 5));

    JPanel row = new JPanel(new GridLayout());
    row.add(new SaneLabelField(get("m.proxy_host")), BorderLayout.WEST);
    row.add(_host = new SaneTextField());
    add(row);

    row = new JPanel(new GridLayout());
    row.add(new SaneLabelField(get("m.proxy_port")), BorderLayout.WEST);
    row.add(_port = new SaneTextField());
    add(row);

    add(new Spacer(5, 5));

    row = new JPanel(new GridLayout());
    row.add(new SaneLabelField(get("m.proxy_auth_required")), BorderLayout.WEST);
    _checkBox = new JCheckBox();
    row.add(_checkBox);
    add(row);

    row = new JPanel(new GridLayout());
    row.add(new SaneLabelField(get("m.proxy_username")), BorderLayout.WEST);
    _username = new SaneTextField();
    _username.setEnabled(false);
    row.add(_username);
    add(row);

    row = new JPanel(new GridLayout());
    row.add(new SaneLabelField(get("m.proxy_password")), BorderLayout.WEST);
    _password = new SanePasswordField();
    _password.setEnabled(false);
    row.add(_password);
    add(row);

    _checkBox.addItemListener(_itemListener);

    add(new Spacer(5, 5));

    row = GroupLayout.makeButtonBox(GroupLayout.CENTER);
    JButton button;
    row.add(button = new JButton(get("m.proxy_ok")));
    button.setActionCommand("ok");
    button.addActionListener(this);
    row.add(button = new JButton(get("m.proxy_cancel")));
    button.setActionCommand("cancel");
    button.addActionListener(this);
    add(row);

    // set up any existing proxy defaults
    String host = System.getProperty("http.proxyHost");
    if (host != null)
    {
      _host.setText(host);
    }
    String port = System.getProperty("http.proxyPort");
    if (port != null)
    {
      _port.setText(port);
    }
  }

  // documentation inherited
  @Override
  public void addNotify()
  {
    super.addNotify();
    _host.requestFocusInWindow();
  }

  // documentation inherited
  @Override
  public Dimension getPreferredSize()
  {
    // this is annoyingly hardcoded, but we can't just force the width
    // or the JLabel will claim a bogus height thinking it can lay its
    // text out all on one line which will booch the whole UI's
    // preferred size
    return new Dimension(500, 320);
  }

  // documentation inherited from interface
  @Override
  public void actionPerformed(ActionEvent e)
  {
    String cmd = e.getActionCommand();
    if (cmd.equals("ok"))
    {
      // communicate this info back to getdown
      if (_checkBox.isSelected())
      {
      _getdown.configureProxy(_host.getText(), _port.getText(), _username.getText(),
            _password.getPassword());
      }
      else
      {
        _getdown.configureProxy(_host.getText(), _port.getText());
      }
    }
    else
    {
      // they canceled, we're outta here
      System.exit(0);
    }
  }

  /** Used to look up localized messages. */
  protected String get(String key)
  {
    // if this string is tainted, we don't translate it, instead we
    // simply remove the taint character and return it to the caller
    if (MessageUtil.isTainted(key))
    {
      return MessageUtil.untaint(key);
    }
    try
    {
      return _msgs.getString(key);
    }
    catch (MissingResourceException mre)
    {
      log.warning("Missing translation message '" + key + "'.");
      return key;
    }
  }

  protected static class SaneTextField extends JTextField
  {
    @Override
    public Dimension getPreferredSize()
    {
      Dimension d = super.getPreferredSize();
      d.width = Math.max(d.width, 150);
      return d;
    }
  }

  protected static class SanePasswordField extends JPasswordField
  {
    @Override
    public Dimension getPreferredSize()
    {
      Dimension d = super.getPreferredSize();
      d.width = Math.max(d.width, 150);
      return d;
    }
  }

  protected static class SaneLabelField extends JLabel
  {
    public SaneLabelField(String message)
    {
      super(message);
    }

    @Override
    public Dimension getPreferredSize()
    {
      Dimension d = super.getPreferredSize();
      d.width = Math.max(d.width, 200);
      return d;
    }
  }

  protected ItemListener _itemListener = new ItemListener()
  {

    @Override
    public void itemStateChanged(ItemEvent aE)
    {
      if (aE.getStateChange() == ItemEvent.SELECTED)
      {
        _checkBox.setSelected(true);
        _username.setEnabled(true);
        _password.setEnabled(true);
      }
      else
      {
        _checkBox.setSelected(false);
        _username.setEnabled(false);
        _password.setEnabled(false);
      }
    }
  };

  protected Getdown _getdown;
  protected ResourceBundle _msgs;

  protected JTextField _host;
  protected JTextField _port;
  protected JTextField _username;
  protected JPasswordField _password;
  protected JCheckBox _checkBox;
}
