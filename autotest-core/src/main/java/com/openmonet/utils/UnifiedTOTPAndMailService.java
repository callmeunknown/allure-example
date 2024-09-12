//package com.openmonet.utils;
//
//import javax.crypto.Mac;
//import javax.crypto.spec.SecretKeySpec;
//import javax.mail.*;
//import java.io.IOException;
//import java.util.Properties;
//
//import org.bouncycastle.util.encoders.Base32;
//
//
//public class UnifiedTOTPAndMailService {
//    private static String getTextFromMessage(Message message) throws MessagingException, IOException {
//        if (message.getContent() instanceof String) {
//            return ((String) message.getContent());
//        }
//        return null;
//    }
//
//    /**
//     * Получает код GMail путем обращения и входа в почту
//     *
//     * @param email -   смк-код
//     * @param secretKey - секрет кей от почты
//     * @return -   код OTP
//     */
//    public static String OneTimePassword(String email, String secretKey) {
//        String oneTimeCode = null;
//        Properties properties = new Properties();
//        int count = 0;
//        properties.put("mail.store.protocol", "pop3s");
//        properties.put("mail.pop3.host", "pop.gmail.com");
//        properties.put("mail.pop3.port", "995");
//        properties.put("mail.pop3.starttls.enable", "true");
//
//        Session emailSession = Session.getDefaultInstance(properties);
//
//        try (Store store = emailSession.getStore("pop3s");) {
//            store.connect("pop.gmail.com", email, secretKey);
//            try (Folder emailFolder = store.getDefaultFolder().getFolder("INBOX");) {
//                emailFolder.open(Folder.READ_WRITE);
//                Message[] messages = emailFolder.getMessages();
//
//                for (Message message : messages) {
//
//                    String content = getTextFromMessage(message);
//                    if (content != null && message.getSubject() != null && message.getSubject().equals("One time code")) {
//                        message.setFlag(Flags.Flag.DELETED, true);
//                        oneTimeCode = content.trim();
//                        count++;
//                        break;
//                    }
//                }
//
//            }
//            System.out.println("Counter = " + count);
//        } catch (NoSuchProviderException e) {
//            System.err.println("Error: No provider found for pop3s.");
//            e.printStackTrace();
//        } catch (MessagingException | IOException e) {
//            System.err.println("Error processing email messages.");
//            e.printStackTrace();
//        }
//        return oneTimeCode;
//    }
//
//    private static final long TIME_STEP = 30_000; // Time step in milliseconds
//    private static final int TOTP_LENGTH = 6; // TOTP code length
//
//    /**
//     * Получает код TOTP
//     *
//     * @param secretKey - секрет кей от почты
//     * @return - TOTP код
//     */
//    public static String generateTOTP(String secretKey) {
//        byte[] decodedKey = new Base32().decode(secretKey);
//        long timeWindow = System.currentTimeMillis() / TIME_STEP;
//        byte[] data = new byte[8];
//        for (int i = 8; i-- > 0; timeWindow >>>= 8) {
//            data[i] = (byte) timeWindow;
//        }
//        try {
//            Mac mac = Mac.getInstance("HmacSHA1");
//            mac.init(new SecretKeySpec(decodedKey, "HmacSHA1"));
//            byte[] hash = mac.doFinal(data);
//            int offset = hash[hash.length - 1] & 0xF;
//            long truncatedHash = 0;
//            for (int i = 0; i < 4; ++i) {
//                truncatedHash <<= 8;
//                truncatedHash |= (hash[offset + i] & 0xFF);
//            }
//            truncatedHash &= 0x7FFFFFFF;
//            truncatedHash %= (int) Math.pow(10, TOTP_LENGTH);
//            String TOTP = String.format("%0" + TOTP_LENGTH + "d", truncatedHash);
//            return String.format("%0" + TOTP_LENGTH + "d", truncatedHash);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
