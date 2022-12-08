package com.ead.course.controllers;

import com.ead.course.dtos.LessonDto;
import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.services.LessonService;
import com.ead.course.services.ModuleService;
import com.ead.course.specifications.SpecificationTemplate;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@RestController
@RequestMapping("/modules/{moduleId}/lessons")
@CrossOrigin(origins = "*", maxAge = 3600)
public class LessonController {

    @Autowired
    LessonService lessonService;

    @Autowired
    ModuleService moduleService;

    @PostMapping
    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    public ResponseEntity<Object> saveLesson(@PathVariable(value = "moduleId") UUID moduleId,
                                             @RequestBody @Valid LessonDto lessonDto) {

        log.debug("POST saveLesson lessonDto received {} ", lessonDto.toString());
        Optional<ModuleModel> moduleModelOptional = moduleService.findById(moduleId);

        if (moduleModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module not found.");
        }

        var lessonModel = new LessonModel();
        BeanUtils.copyProperties(lessonDto, lessonModel);
        lessonModel.setModule(moduleModelOptional.get());
        lessonModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        lessonService.save(lessonModel);
        log.debug("POST saveLesson lessonId saved {} ", lessonModel.getLessonId());
        log.info("Lesson saved successfully lessonId {} ", lessonModel.getLessonId());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(lessonModel);
    }

    @DeleteMapping("/{lessonId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    public ResponseEntity<Object> deleteLesson(@PathVariable(value = "moduleId") UUID moduleId,
                                               @PathVariable(value = "lessonId") UUID lessonId) {

        log.debug("DELETE deleteLesson lessonId received {} ", lessonId);
        Optional<LessonModel> lessonModelOptional = lessonService.findLessonIntoModule(moduleId, lessonId);

        if (lessonModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lesson not found for this Module.");
        }

        lessonService.delete(lessonModelOptional.get());
        log.debug("DELETE deleteLesson lessonId deleted {} ", lessonId);
        log.info("Lesson deleted successfully lessonId {} ", lessonId);
        return ResponseEntity.status(HttpStatus.OK).body("Lesson deleted successfully.");
    }

    @PutMapping("/{lessonId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    public ResponseEntity<Object> updateLesson(@PathVariable(value = "moduleId") UUID moduleId,
                                               @PathVariable(value = "lessonId") UUID lessonId,
                                               @RequestBody @Valid LessonDto lessonDto) {
       log.debug("PUT updateLesson lessonDto received {} ", lessonDto.toString());

        Optional<LessonModel> lessonModelOptional = lessonService.findLessonIntoModule(moduleId, lessonId);

        if (lessonModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lesson not found for this Module.");
        }

        var lessonModel = lessonModelOptional.get();
        lessonModel.setTitle(lessonDto.getTitle());
        lessonModel.setDescription(lessonDto.getDescription());
        lessonModel.setVideoUrl(lessonDto.getVideoUrl());
        lessonService.save(lessonModel);
        log.debug("PUT updateLesson lessonId saved {} ", lessonModel.getLessonId());
        log.info("Lesson updated successfully lessonId {} ", lessonModel.getLessonId());
        return ResponseEntity.status(HttpStatus.OK).body(lessonModel);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('STUDENT')")
    public ResponseEntity<Page<LessonModel>> getAllLessons(@PathVariable(value = "moduleId") UUID moduleId,
                                                           SpecificationTemplate.LessonSpec spec,
                                                           @PageableDefault(page = 0, size = 10, sort = "lessonId", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(lessonService.findAllByModule(SpecificationTemplate.lessonModuleId(moduleId)
                        .and(spec), pageable));
    }

    @GetMapping("/{lessonId}")
    @PreAuthorize("hasAnyRole('STUDENT')")
    public ResponseEntity<Object> getOneLesson(@PathVariable(value = "moduleId") UUID moduleId,
                                               @PathVariable(value = "lessonId") UUID lessonId) {
        Optional<LessonModel> lessonModelOptional = lessonService.findLessonIntoModule(moduleId, lessonId);

        if (lessonModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lesson not found for this Module.");
        }

        return ResponseEntity.status(HttpStatus.OK).body(lessonModelOptional.get());
    }

}
