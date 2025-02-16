package com.hushpdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class HushPDFApp {
    private JFrame frame;
    private JTextField passwordField;
    private JFileChooser fileChooser;
    private List<File> selectedFiles;

    public HushPDFApp() {
        frame = new JFrame("HushPDF - PDF 암호화 프로그램");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLayout(new GridLayout(3, 1));

        JButton selectButton = new JButton("PDF 파일 선택");
        passwordField = new JTextField();
        JButton encryptButton = new JButton("암호 설정");

        selectButton.addActionListener(this::selectFiles);
        encryptButton.addActionListener(this::encryptPDFs);

        frame.add(selectButton);
        frame.add(passwordField);
        frame.add(encryptButton);
        frame.setVisible(true);
    }

    private void selectFiles(ActionEvent e) {
        fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int returnValue = fileChooser.showOpenDialog(frame);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedFiles = List.of(fileChooser.getSelectedFiles());
        }
    }

    private void encryptPDFs(ActionEvent e) {
        if (selectedFiles == null || selectedFiles.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "파일을 선택하세요.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String password = passwordField.getText();
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "암호를 입력하세요.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (File file : selectedFiles) {
            try (PDDocument document = PDDocument.load(file)) {
                AccessPermission accessPermission = new AccessPermission();
                StandardProtectionPolicy spp = new StandardProtectionPolicy(password, password, accessPermission);
                spp.setEncryptionKeyLength(128);
                document.protect(spp);
                document.save(file.getParent() + "/encrypted_" + file.getName());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "오류 발생: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            }
        }
        JOptionPane.showMessageDialog(frame, "암호화 완료!", "완료", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HushPDFApp::new);
    }
}
