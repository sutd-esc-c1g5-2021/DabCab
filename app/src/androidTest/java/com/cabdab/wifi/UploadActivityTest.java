package com.example.cabdab;

import junit.framework.TestCase;

import org.junit.Test;

import static org.junit.Assert.*;

public class UploadActivityTest extends TestCase {
    @Test public void testDuplicateName(){
        assertEquals(false, new UploadActivity().duplicateName());
    }


}