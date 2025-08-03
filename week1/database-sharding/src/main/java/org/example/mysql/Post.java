package org.example.mysql;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class Post {
    private int id;
    private String owner;
    private String content;
}