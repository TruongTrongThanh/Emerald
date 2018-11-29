package org.emerald.comicapi.validator.sequence;

import javax.validation.GroupSequence;

@GroupSequence({FieldLevel.class, ClassLevel.class})
public interface ValidationSequence {
}
