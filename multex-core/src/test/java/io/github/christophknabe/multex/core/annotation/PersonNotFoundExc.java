package io.github.christophknabe.multex.core.annotation;

import io.github.christophknabe.multex.core.Exc;
import io.github.christophknabe.multex.tool.TextPattern;

@TextPattern("Person with username {0} not found in database")
public class PersonNotFoundExc extends Exc {
    public PersonNotFoundExc(final String username){
        super(null, username);
    }
}
