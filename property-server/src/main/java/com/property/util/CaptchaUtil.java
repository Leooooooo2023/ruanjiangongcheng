package com.property.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 图形验证码工具
 */
public class CaptchaUtil {

    private static final int WIDTH = 130;
    private static final int HEIGHT = 48;
    private static final int CODE_LENGTH = 4;
    private static final String CHARS = "23456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz";
    private static final Random RANDOM = new Random();

    /** 内存存储验证码，key -> {code, expireTime} */
    private static final ConcurrentHashMap<String, CodeEntry> STORE = new ConcurrentHashMap<>();

    /** 验证码有效期 5 分钟 */
    private static final long EXPIRE_MS = 5 * 60 * 1000;

    /**
     * 生成验证码，返回 key 和 Base64 图片
     */
    public static CaptchaResult generate() {
        String code = randomCode();
        String key = UUID.randomUUID().toString().replace("-", "");

        // 清理过期条目
        long now = System.currentTimeMillis();
        STORE.entrySet().removeIf(e -> e.getValue().expireTime < now);

        STORE.put(key, new CodeEntry(code, now + EXPIRE_MS));

        BufferedImage image = createImage(code);
        String base64 = imageToBase64(image);

        return new CaptchaResult(key, base64);
    }

    /**
     * 校验验证码，校验后立即删除
     */
    public static boolean verify(String key, String code) {
        if (key == null || code == null) return false;
        CodeEntry entry = STORE.remove(key);
        if (entry == null) return false;
        if (System.currentTimeMillis() > entry.expireTime) return false;
        return entry.code.equalsIgnoreCase(code);
    }

    private static String randomCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }
        return sb.toString();
    }

    private static BufferedImage createImage(String code) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 背景
        g.setColor(new Color(240, 245, 250));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // 干扰线
        g.setColor(new Color(180, 200, 220));
        for (int i = 0; i < 8; i++) {
            int x1 = RANDOM.nextInt(WIDTH);
            int y1 = RANDOM.nextInt(HEIGHT);
            int x2 = RANDOM.nextInt(WIDTH);
            int y2 = RANDOM.nextInt(HEIGHT);
            g.drawLine(x1, y1, x2, y2);
        }

        // 干扰点
        for (int i = 0; i < 60; i++) {
            int x = RANDOM.nextInt(WIDTH);
            int y = RANDOM.nextInt(HEIGHT);
            image.setRGB(x, y, new Color(150, 180, 210).getRGB());
        }

        // 文字
        Font[] fonts = {
            new Font("Arial", Font.BOLD, 28),
            new Font("Courier New", Font.BOLD, 28)
        };
        for (int i = 0; i < code.length(); i++) {
            g.setFont(fonts[RANDOM.nextInt(fonts.length)]);
            g.setColor(new Color(30 + RANDOM.nextInt(80), 60 + RANDOM.nextInt(80), 140 + RANDOM.nextInt(60)));
            int x = 15 + i * 28 + RANDOM.nextInt(5);
            int y = 33 + RANDOM.nextInt(8) - 4;
            double angle = (RANDOM.nextDouble() - 0.5) * 0.3;
            g.rotate(angle, x, y);
            g.drawString(String.valueOf(code.charAt(i)), x, y);
            g.rotate(-angle, x, y);
        }

        g.dispose();
        return image;
    }

    private static String imageToBase64(BufferedImage image) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("验证码图片生成失败", e);
        }
    }

    private static class CodeEntry {
        String code;
        long expireTime;
        CodeEntry(String code, long expireTime) {
            this.code = code;
            this.expireTime = expireTime;
        }
    }

    public static class CaptchaResult {
        private final String key;
        private final String imageBase64;
        public CaptchaResult(String key, String imageBase64) {
            this.key = key;
            this.imageBase64 = imageBase64;
        }
        public String getKey() { return key; }
        public String getImageBase64() { return imageBase64; }
    }
}
