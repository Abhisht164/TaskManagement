package com.learning.filters.TaskManagement.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TaskDto {

    Long id;

    String title;

    String description;
}
