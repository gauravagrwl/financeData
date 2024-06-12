package org.gauravagrwl.financeData.helper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DuplicateStatementRecords {
    private int count;
    private List<String> ids;
}
