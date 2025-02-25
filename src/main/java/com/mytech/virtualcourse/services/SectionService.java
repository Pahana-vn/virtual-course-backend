package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.SectionDTO;
import com.mytech.virtualcourse.mappers.SectionMapper;
import com.mytech.virtualcourse.repositories.SectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SectionService {

    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private SectionMapper sectionMapper;

    public List<SectionDTO> getSectionsByCourseId(Long courseId) {
        return sectionRepository.findByCourseId(courseId).stream()
                .map(sectionMapper::sectionToSectionDTO)
                .collect(Collectors.toList());
    }
}
