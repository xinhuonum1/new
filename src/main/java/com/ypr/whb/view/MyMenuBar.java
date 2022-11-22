package com.ypr.whb.view;

import com.ypr.whb.connect.MyIPTool;
import com.ypr.whb.data.Algorithm;
import com.ypr.whb.game.CountDown;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 界面显示 之 菜单栏
 */
public class MyMenuBar extends JMenuBar {
    private static final long serialVersionUID = 1L;
    JMenuItem checkIP,  exit,  setTime, setLevel;

    public MyMenuBar() {
        UIManager.put("Menu.font", new Font("宋体", Font.BOLD, 22));
        UIManager.put("MenuItem.font", new Font("宋体", Font.BOLD, 22));

        JMenu menu = new JMenu("菜单(F)");
        menu.setMnemonic('f'); // 助记符

        JMenu setting = new JMenu("设置(S)");
        setting.setMnemonic('s'); // 助记符

        checkIP = new JMenuItem("查看本机IP");
        exit = new JMenuItem("退出");

        setLevel = new JMenuItem("难度设置");
        setTime = new JMenuItem("倒计时设置");

        menu.add(checkIP);
        menu.add(exit);

        setting.add(setLevel);
        setting.add(setTime);

        this.add(menu);
        this.add(setting);
        addListener();
    }

    public void addListener() {
        exit.addActionListener(event -> MainFrame.close());

        checkIP.addActionListener(event -> {
            String localIP = "本机所有IP地址:";
            List<String> res = MyIPTool.getAllLocalHostIP();
            String allIp = res.stream().collect(Collectors.joining("\n"));
            localIP = localIP + "\n" + allIp;
            JOptionPane.showMessageDialog(MainFrame.mainFrame, localIP, "查看本机IP", JOptionPane.INFORMATION_MESSAGE);
        });

        setTime.addActionListener(event -> {
            String input = JOptionPane.showInputDialog("请输入超时时间(秒)", "30");
            try {
                int time = Integer.valueOf(input);
                CountDown.startTiming(time);
            } catch (Exception e) {
            }
        });

        setLevel.addActionListener(event -> {
            Object[] options = {"初级", "中级", "高级"};
            int m = JOptionPane
                    .showOptionDialog(MainFrame.mainFrame, "请选择AI智能程度", "请选择",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE, null, options,
                            options[0]);

        });

    }
}
