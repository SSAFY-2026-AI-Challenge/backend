package com.example.seed.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginResponse {

    private final String accessToken;
    private final UserResponse user;

    public LoginResponse(
            String accessToken,
            UserResponse user
    ) {
        this.accessToken = accessToken;
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public UserResponse getUser() {
        return user;
    }

    public static class UserResponse {

        private final Integer id;
        private final String name;
        private final String role;
        private final String job;

        @JsonProperty("avatar_url")
        private final String avatarUrl;

        @JsonProperty("class")
        private final String className;

        private final Integer classroomId;

        public UserResponse(
                Integer id,
                String name,
                String role,
                String job,
                String avatarUrl,
                String className,
                Integer classroomId
        ) {
            this.id = id;
            this.name = name;
            this.role = role;
            this.job = job;
            this.avatarUrl = avatarUrl;
            this.className = className;
            this.classroomId = classroomId;
        }

        public Integer getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getRole() {
            return role;
        }

        public String getJob() {
            return job;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public String getClassName() {
            return className;
        }

        public Integer getClassroomId() {
            return classroomId;
        }
    }
}