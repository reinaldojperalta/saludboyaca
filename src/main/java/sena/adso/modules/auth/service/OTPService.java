package sena.adso.modules.auth.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import sena.adso.core.util.DatabaseConfig;
import sena.adso.modules.auth.dao.OTPTokenDAO;

/**
 * Servicio para generación y envío de códigos OTP.
 * 
 * En local usa MailHog (sin autenticación).
 * En producción usa Gmail SMTP con App Password.
 */
public final class OTPService {

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final OTPTokenDAO otpTokenDAO;

    public OTPService() {
        this.otpTokenDAO = new OTPTokenDAO();
    }

    /**
     * Genera un código OTP numérico de 6 dígitos.
     */
    public String generarOTP() {
        StringBuilder sb = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++) {
            sb.append(SECURE_RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    /**
     * Genera, guarda y envía un OTP al usuario.
     * 
     * @param idUsuario ID del usuario en BD
     * @param email     Email destino
     * @param asunto    Asunto del correo (i18n)
     * @param cuerpo    Cuerpo del correo con el código interpolado
     */
    // sena.adso.modules.auth.service.OTPService.java

    public void generarYEnviar(int idUsuario, String email, String asunto, String plantillaCuerpo) {
        // 1. Generar el código único (Única fuente de verdad)
        String codigo = generarOTP();

        // 2. Definir tiempo de expiración
        LocalDateTime expiraEn = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);

        // 3. Persistir en BD (Capa DAO)
        // Nota: El TransactionManager ya debe estar gestionando la conexión
        otpTokenDAO.insertar(idUsuario, codigo, expiraEn);

        // 4. Inyectar el código en la plantilla del mensaje
        // Reemplazamos el marcador {0} que viene del archivo de propiedades
        String cuerpoFinal = plantillaCuerpo.replace("{0}", codigo);

        // 5. Enviar el email con el código CORRECTO
        enviarEmail(email, asunto, cuerpoFinal);

        // Opcional: Log de depuración (solo en desarrollo)
        System.out.println("[OTP_DEBUG] Usuario: " + idUsuario + " | Código enviado y guardado: " + codigo);
    }

    /**
     * Valida un OTP ingresado por el usuario.
     * 
     * @return true si es válido y no ha expirado
     */
    public boolean validarOTP(int idUsuario, String codigo) {
        return otpTokenDAO.validar(idUsuario, codigo);
    }

    /**
     * Marca un OTP como usado después de validación exitosa.
     */
    public void marcarUsado(int idUsuario, String codigo) {
        otpTokenDAO.marcarUsado(idUsuario, codigo);
    }

    /**
     * Verifica si el usuario ha excedido el límite de intentos.
     */
    public boolean haExcedidoIntentos(int idUsuario, int maxIntentos) {
        return otpTokenDAO.contarIntentosFallidos(idUsuario, OTP_EXPIRY_MINUTES) >= maxIntentos;
    }

    // ============================================================
    // ENVÍO DE EMAIL
    // ============================================================

    private void enviarEmail(String destinatario, String asunto, String cuerpo) {
        String host = DatabaseConfig.getEnv("EMAIL_HOST", "localhost");
        int port = Integer.parseInt(DatabaseConfig.getEnv("EMAIL_PORT", "1025"));
        String user = DatabaseConfig.getEnv("EMAIL_USER", "");
        String pass = DatabaseConfig.getEnv("EMAIL_PASS", "");

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", String.valueOf(port));

        // Solo auth si hay usuario configurado (producción)
        boolean useAuth = !user.isBlank();
        if (useAuth) {
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
        }

        Session session = Session.getInstance(props, useAuth ? new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, pass);
            }
        } : null);

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user.isBlank() ? "noreply@saludboyaca.local" : user));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject(asunto);
            message.setText(cuerpo);

            Transport.send(message);

        } catch (MessagingException e) {
            System.err.println("Error enviando OTP a " + destinatario + ": " + e.getMessage());
            // No lanzamos excepción para no romper el flujo de login
            // El OTP sigue guardado en BD, el usuario puede reenviar
        }

    }

    public void limpiarIntentosFallidos(int idUsuario) {
        otpTokenDAO.eliminarPorUsuario(idUsuario);
    }

}