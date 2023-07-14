package com.example.advanced_server.service;

import java.util.UUID;

import com.example.advanced_server.dto.newsDto.CreateNewsSuccessResponse;
import com.example.advanced_server.dto.newsDto.NewsDto;


public interface NewsService {
    CreateNewsSuccessResponse createNews(UUID id, NewsDto newsDto);
}