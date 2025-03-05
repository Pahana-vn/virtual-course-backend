package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.*;
import com.mytech.virtualcourse.entities.*;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CurriculumMapper {

    // Chuyển từ Entity sang DTO
    @Mapping(source = "course.id", target = "courseId")
    SectionDTO sectionToSectionDTO(Section section);

    @Mapping(source = "section.id", target = "sectionId")
    LectureDTO lectureToLectureDTO(Lecture lecture);

    @Mapping(source = "lecture.id", target = "lectureId")
    ArticleDTO articleToArticleDTO(Article article);

    List<SectionDTO> sectionsToSectionDTOs(List<Section> sections);
    List<LectureDTO> lecturesToLectureDTOs(List<Lecture> lectures);
    List<ArticleDTO> articlesToArticleDTOs(List<Article> articles);

    // Chuyển từ DTO sang Entity
    @Mapping(target = "id", ignore = true)  // Ignore ID nếu không có
    Section sectionDTOToSection(SectionDTO sectionDTO);

    @Mapping(target = "id", ignore = true)
    Lecture lectureDTOToLecture(LectureDTO lectureDTO);

    @Mapping(target = "id", ignore = true)
    Article articleDTOToArticle(ArticleDTO articleDTO);

    List<Section> sectionDTOsToSections(List<SectionDTO> sectionDTOs);
    List<Lecture> lectureDTOsToLectures(List<LectureDTO> lectureDTOs);
    List<Article> articleDTOsToArticles(List<ArticleDTO> articleDTOs);
}
