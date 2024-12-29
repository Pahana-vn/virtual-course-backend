package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.ArticleDTO;
import com.mytech.virtualcourse.entities.Article;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface ArticleMapper {

    ArticleMapper INSTANCE = Mappers.getMapper(ArticleMapper.class);

    // Map từ Entity sang DTO
//    @Mapping(source = "lecture.id", target = "lectureId")
    ArticleDTO articleToArticleDTO(Article article);

    // Map từ DTO sang Entity
//    @Mapping(source = "lectureId", target = "lecture.id")
    Article articleDTOToArticle(ArticleDTO articleDTO);
}
