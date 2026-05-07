package sena.adso.core.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.imageio.ImageIO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Generador de CAPTCHA simple para el módulo de consulta pública.
 * 
 * Genera imágenes PNG con código alfanumérico de 6 caracteres.
 * El código se almacena en sesión con timestamp de expiración (5 minutos).
 * 
 * NO usar en rutas protegidas por auth — solo en consulta pública.
 */
public class SimpleCaptcha {

    private static final String SESSION_KEY = "captchaCode";
    private static final String SESSION_EXPIRY = "captchaExpiry";
    private static final long EXPIRY_MS = 5 * 60 * 1000; // 5 minutos
    private static final int CODE_LENGTH = 6;
    private static final int IMAGE_WIDTH = 200;
    private static final int IMAGE_HEIGHT = 60;

    private static final String CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // Sin I, O, 0, 1 para evitar confusión
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Genera un nuevo código CAPTCHA, lo guarda en sesión y retorna la imagen como
     * Base64.
     * 
     * @return String Base64 de la imagen PNG (data:image/png;base64,...)
     */
    public static String generate(HttpServletRequest req) {
        String code = generateCode();
        storeInSession(req, code);

        BufferedImage image = createImage(code);
        String base64 = imageToBase64(image);

        return "data:image/png;base64," + base64;
    }

    /**
     * Valida el código ingresado contra el almacenado en sesión.
     * 
     * @param userInput Código que escribió el usuario
     * @return true si coincide y no ha expirado
     */
    public static boolean validate(HttpServletRequest req, String userInput) {
        if (userInput == null || userInput.isBlank()) {
            return false;
        }

        HttpSession session = req.getSession(false);
        if (session == null) {
            return false;
        }

        String storedCode = (String) session.getAttribute(SESSION_KEY);
        Long expiry = (Long) session.getAttribute(SESSION_EXPIRY);

        if (storedCode == null || expiry == null) {
            return false;
        }

        // Verificar expiración
        if (System.currentTimeMillis() > expiry) {
            clearSession(session);
            return false;
        }

        // Comparación case-insensitive, sin espacios
        boolean valid = storedCode.equalsIgnoreCase(userInput.trim());

        // Limpiar después de validar (one-time use)
        if (valid) {
            clearSession(session);
        }

        return valid;
    }

    /**
     * Invalida el CAPTCHA actual en sesión.
     */
    public static void invalidate(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            clearSession(session);
        }
    }

    // ============================================================
    // PRIVADOS
    // ============================================================

    private static String generateCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    private static void storeInSession(HttpServletRequest req, String code) {
        HttpSession session = req.getSession();
        session.setAttribute(SESSION_KEY, code);
        session.setAttribute(SESSION_EXPIRY, System.currentTimeMillis() + EXPIRY_MS);
    }

    private static void clearSession(HttpSession session) {
        session.removeAttribute(SESSION_KEY);
        session.removeAttribute(SESSION_EXPIRY);
    }

    private static BufferedImage createImage(String code) {
        BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // Antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        // Fondo gradiente sutil
        g2d.setColor(new Color(234, 240, 247)); // gris-hielo
        g2d.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);

        // Líneas de ruido
        g2d.setColor(new Color(189, 195, 199, 120)); // gris-borde con transparencia
        for (int i = 0; i < 8; i++) {
            int x1 = RANDOM.nextInt(IMAGE_WIDTH);
            int y1 = RANDOM.nextInt(IMAGE_HEIGHT);
            int x2 = RANDOM.nextInt(IMAGE_WIDTH);
            int y2 = RANDOM.nextInt(IMAGE_HEIGHT);
            g2d.drawLine(x1, y1, x2, y2);
        }

        // Puntos de ruido
        for (int i = 0; i < 30; i++) {
            int x = RANDOM.nextInt(IMAGE_WIDTH);
            int y = RANDOM.nextInt(IMAGE_HEIGHT);
            g2d.setColor(new Color(
                    RANDOM.nextInt(100),
                    RANDOM.nextInt(100),
                    RANDOM.nextInt(100),
                    150));
            g2d.fillOval(x, y, 2, 2);
        }

        // Texto del código
        g2d.setFont(new Font("Courier New", Font.BOLD, 32));
        g2d.setColor(new Color(26, 82, 118)); // azul-salud

        // Posicionar caracteres con ligera rotación individual
        int startX = 20;
        for (int i = 0; i < code.length(); i++) {
            String ch = String.valueOf(code.charAt(i));
            int x = startX + (i * 28);
            int y = 42 + RANDOM.nextInt(8) - 4;

            double rotation = (RANDOM.nextDouble() - 0.5) * 0.3; // ±0.15 rad
            g2d.rotate(rotation, x + 10, y);
            g2d.drawString(ch, x, y);
            g2d.rotate(-rotation, x + 10, y);
        }

        // Borde sutil
        g2d.setColor(new Color(26, 82, 118, 80));
        g2d.drawRect(0, 0, IMAGE_WIDTH - 1, IMAGE_HEIGHT - 1);

        g2d.dispose();
        return image;
    }

    private static String imageToBase64(BufferedImage image) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "PNG", baos);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Error generando CAPTCHA", e);
        }
    }
}