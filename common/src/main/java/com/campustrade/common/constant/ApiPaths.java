package com.campustrade.common.constant;

/**
 * Common API path constants used across gateway and services.
 */
public final class ApiPaths {
    public static final String API_PREFIX = "/api";
    public static final String V1 = API_PREFIX + "/v1";

    private ApiPaths() {
    }

    public static final class User {
        public static final String BASE = V1 + "/users";
        public static final String LOGIN = BASE + "/login";
        public static final String REGISTER = BASE + "/register";
        public static final String PROFILE = BASE + "/{userId}";

        private User() {
        }
    }

    public static final class Product {
        public static final String BASE = V1 + "/products";
        public static final String DETAIL = BASE + "/{productId}";
        public static final String PAGE = BASE + "/page";

        private Product() {
        }
    }

    public static final class Favorite {
        public static final String BASE = V1 + "/favorites";
        public static final String USER_FAVORITES = BASE + "/users/{userId}";

        private Favorite() {
        }
    }

    public static final class Message {
        public static final String BASE = V1 + "/messages";
        public static final String CONVERSATIONS = BASE + "/conversations";
        public static final String CONVERSATION_DETAIL = CONVERSATIONS + "/{conversationId}";

        private Message() {
        }
    }
}
