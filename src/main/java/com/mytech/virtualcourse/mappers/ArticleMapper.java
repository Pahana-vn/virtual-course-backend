package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.ArticleDTO;
import com.mytech.virtualcourse.entities.Article;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface ArticleMapper {
    ArticleDTO articleToArticleDTO(Article article);
}
