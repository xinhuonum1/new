package com.ypr.whb;

import com.ypr.whb.view.MainFrame;

/**
 * 主类，主函数类
 *
 * @author chaos
 */
public class GobangMain {

    public static void main(String[] args) {
        MainFrame mainFrame = new MainFrame();
        mainFrame.setVisible(true);
        // 界面加载完后，加载数据
        MainFrame.init();
    }
}
