package com.daily.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.daily.entity.DiaryImage;

public interface DiaryImageRepository extends JpaRepository<DiaryImage, Long> {

}
