package com.dndgus482.guestbook.dto;

import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
public class PageResultDTO<DTO, EN> {

    private List<DTO> dtoList;

    private int totalPage;

    //현재 페이지 번호
    private int page;

    private int size;

    //현재에서 시작페이지 번호, 끝페이지 번호
    private int start, end;

    // prev next 가능여부
    private boolean prev, next;

    private List<Integer> pageList;


    public PageResultDTO(Page<EN> result, Function<EN, DTO> fn) {
        dtoList = result.stream().map(fn).collect(Collectors.toList());
        totalPage = result.getTotalPages();
        makePageList(result.getPageable());
    }

    private void makePageList(Pageable pageable) {
        this.page = pageable.getPageNumber() + 1;
        this.size = pageable.getPageSize();

        int tempEnd = (int) (Math.ceil(page / 10.0)) * 10;

        start = tempEnd - 9;
        prev = start > 1;
        end = Math.min(totalPage, tempEnd);
        next = totalPage > tempEnd;
        pageList = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
    }
}
