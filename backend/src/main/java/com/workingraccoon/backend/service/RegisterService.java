package com.workingraccoon.backend.service;

import com.workingraccoon.backend.repository.RegisterRepository;
import com.workingraccoon.backend.util.XssUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class RegisterService {

    @Autowired
    private RegisterRepository registerRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public void register(String username, String phone, String email,
                         String password, String intro, MultipartFile coverImage) throws Exception {

        if (registerRepository.isPhoneExists(phone)) {
            throw new Exception("手機號碼已被註冊");
        }

        String encodedPassword = passwordEncoder.encode(password);
        String safeUsername = XssUtils.sanitize(username);
        String safeIntro = XssUtils.sanitize(intro);

        String coverImagePath = null;
        if (coverImage != null && !coverImage.isEmpty()) {
            String uploadDir = "uploads/covers/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String fileName = System.currentTimeMillis() + "_" + coverImage.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + fileName);
            Files.write(filePath, coverImage.getBytes());
            coverImagePath = "/" + uploadDir + fileName;
        }

        registerRepository.createUser(safeUsername, phone, email, encodedPassword, safeIntro, coverImagePath);
    }
}