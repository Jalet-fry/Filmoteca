package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
@Builder
public class Film {
    private int id;
    private String title;
    private String link;
    private int year;
}
/*
//New not good
package org.example.unknowed.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class Film {
    private String title;
}

 */


/*
package org.example.unknowed.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class Film {
    private Long id;
    private String title;
    private String genre;
    private int year;
}
 */