package com.camaja.catapp.services;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class AzureBlobService {

    @Value("${azure.storage.connection-string}")
    private String connectionString;

    @Value("${azure.storage.container-name}")
    private String containerName;

    public String uploadFile(MultipartFile file) throws IOException {

        BlobContainerClient containerClient =
                new BlobContainerClientBuilder()
                        .connectionString(connectionString)
                        .containerName(containerName)
                        .buildClient();

        if (!containerClient.exists()) {
            containerClient.create();
        }

        String blobName = UUID.randomUUID() + "-" + file.getOriginalFilename();

        BlobClient blobClient = containerClient.getBlobClient(blobName);

        blobClient.upload(file.getInputStream(), file.getSize(), true);

        return blobClient.getBlobUrl(); // store this in DB
    }
}