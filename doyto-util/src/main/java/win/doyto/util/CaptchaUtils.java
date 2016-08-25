package win.doyto.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

/**
 * 类描述。
 *
 * @author Yuanzhen on 2015-08-02.
 */
public class CaptchaUtils {
    public static BufferedImage getImage(String captcha) {
        // 生成验证码，并存在SESSION中

        //在内存中创建图象
        int width = 80, height = 20;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        //获取图形上下文
        Graphics g = image.getGraphics();
        //生成随机类
        Random random = new Random();
        //设定背景色
        g.setColor(getRandomColor().brighter());
        g.fillRect(0, 0, width, height);
        //设定字体
        g.setFont(new Font("Courier New", Font.BOLD, 20));
        //随机产生155条干扰线，使图象中的认证码不易被其它程序探测到
        g.setColor(getRandomColor());
        for (int i = 0; i < 31; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            g.drawLine(x, y, x + xl, y + yl);
        }
        //取随机产生的认证码(6位数字)
        for (int i = 0, len = captcha.length(); i < len; i++) {
            // 将认证码显示到图象中
            g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
            //调用函数出来的颜色相同，可能是因为种子太接近，所以只能直接生成
            g.drawString(captcha.substring(i, i + 1), 13 * i + 10, 16);
        }
        //图象生效
        g.dispose();
        return image;
    }

    public static byte[] toByteArray(String captcha) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageOutputStream imageOut = null;
        try {
            imageOut = ImageIO.createImageOutputStream(output);
            ImageIO.write(getImage(captcha), "JPEG", imageOut);
            imageOut.close();
        } catch (IOException e) { // will never happen
        }
        return (output.toByteArray());
    }

    /*
    * 给定范围获得随机颜色
    */
    private static Color getRandomColor() {
        Random random = new Random();

        int red = random.nextInt(150) + 50;
        int green = random.nextInt(150) + 50;
        int blue = random.nextInt(150) + 50;

        return new Color(red, green, blue);
    }
}
