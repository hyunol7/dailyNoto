package com.daily.service;

import org.springframework.web.multipart.MultipartFile;

import com.daily.dto.DiaryImageDTO;
import com.daily.dto.UploadDTO;
import com.daily.entity.Diary;

public interface FileStorageService {
	UploadDTO storeFile(MultipartFile file);

	void saveImage(DiaryImageDTO imageDTO, Diary diary);

}
