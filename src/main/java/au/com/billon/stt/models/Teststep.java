package au.com.billon.stt.models;

import java.util.Date;
import java.util.List;

/**
 * Created by Zheng on 7/07/2015.
 */
public class Teststep {
    private String name;
    private String description;
    private Date created;
    private Date updated;
    private List<TeststepProperty> properties;
}
