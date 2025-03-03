package com.mytech.virtualcourse.services;


import com.mytech.virtualcourse.dtos.ArticleDTO;
import com.mytech.virtualcourse.dtos.CurriculumImportRequest;
import com.mytech.virtualcourse.dtos.LectureDTO;
import com.mytech.virtualcourse.dtos.SectionDTO;
import com.mytech.virtualcourse.entities.*;
import com.mytech.virtualcourse.mappers.CurriculumMapper;
import com.mytech.virtualcourse.repositories.ArticleRepository;
import com.mytech.virtualcourse.repositories.CourseRepository;
import com.mytech.virtualcourse.repositories.LectureRepository;
import com.mytech.virtualcourse.repositories.SectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CurriculumService {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private LectureRepository lectureRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private CurriculumMapper curriculumMapper;

    public List<Section> importCurriculum(CurriculumImportRequest request) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        List<Section> sections = new ArrayList<>();

        for (SectionDTO sectionDTO : request.getSections()) {
            Section section = sectionDTO.getId() != null ?
                    sectionRepository.findById(sectionDTO.getId()).orElse(new Section()) :
                    new Section();

            section.setTitleSection(sectionDTO.getTitleSection());
            section.setCourse(course);

            List<Lecture> lectures = new ArrayList<>();
            for (LectureDTO lectureDTO : sectionDTO.getLectures()) {
                Lecture lecture = lectureDTO.getId() != null ?
                        lectureRepository.findById(lectureDTO.getId()).orElse(new Lecture()) :
                        new Lecture();

                lecture.setTitleLecture(lectureDTO.getTitleLecture());
                lecture.setLectureVideo(lectureDTO.getLectureVideo());
                lecture.setSection(section);

                List<Article> articles = new ArrayList<>();
                for (ArticleDTO articleDTO : lectureDTO.getArticles()) {
                    Article article = articleDTO.getId() != null ?
                            articleRepository.findById(articleDTO.getId()).orElse(new Article()) :
                            new Article();

                    article.setFileUrl(articleDTO.getFileUrl());
                    article.setContent(articleDTO.getContent());
                    article.setLecture(lecture);
                    articles.add(article);
                }

                lecture.setArticles(articles);
                lectures.add(lecture);
            }

            section.setLectures(lectures);
            sections.add(section);
        }

        return sectionRepository.saveAll(sections);
    }

    public List<SectionDTO> exportCurriculum(Long courseId) {
        List<Section> sections = sectionRepository.findByCourseId(courseId);
        return curriculumMapper.sectionsToSectionDTOs(sections);
    }
}
