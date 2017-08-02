package com.flipkart.rabbitmqspoutexample;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by samkit.shah on 31/07/17.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContentMetadata implements Serializable {
    String subViewType;
    String storeId;
    String pcId;
    SherlockContentType sherlockContentType;
}
