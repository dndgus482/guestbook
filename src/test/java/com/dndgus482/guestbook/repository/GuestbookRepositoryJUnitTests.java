package com.dndgus482.guestbook.repository;

import com.dndgus482.guestbook.entity.Guestbook;
import com.dndgus482.guestbook.entity.QGuestbook;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application-test.properties")
@DataJpaTest
public class GuestbookRepositoryJUnitTests {

    @Autowired
    private GuestbookRepository guestbookRepository;

    @BeforeEach
    public void setup() {
        IntStream.rangeClosed(1, 100).forEach(i -> {
            Guestbook guestbook = Guestbook.builder()
                    .title("Title..." + i)
                    .content("Content..." + i)
                    .writer("user" + (i % 10))
                    .build();

            guestbookRepository.save(guestbook);
        });
    }

    @Test
    public void updateTest() {
        Optional<Guestbook> result1 = guestbookRepository.findById(50L);

        if (result1.isEmpty()) {
            fail(); // 테스트 이전에 insert를 했는데 엔티티가 없으므로
        }
        Guestbook expected = result1.get();
        expected.changeTitle("Changed Title...");
        expected.changeContent("Changed Content");

        guestbookRepository.save(expected);

        Optional<Guestbook> result2 = guestbookRepository.findById(50L);
        if (result2.isEmpty()) {
            fail(); // 업데이트를 했는데 엔티티가 없으므
        }

        Guestbook actual = result2.get();

        assertEntityEquals(expected, actual);
    }

    @Test
    public void testQuery1() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("gno").descending());

        QGuestbook qGuestbook = QGuestbook.guestbook;

        String keyword = "1";

        BooleanBuilder builder = new BooleanBuilder();
        BooleanExpression expression = qGuestbook.title.contains(keyword);
        builder.and(expression);

        Page<Guestbook> result = guestbookRepository.findAll(builder, pageable);

        assertEquals(20, result.getTotalElements());
    }

    @Test
    public void testQuery2() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("gno").descending());

        QGuestbook qGuestbook = QGuestbook.guestbook;

        String keyword = "2";

        BooleanBuilder builder = new BooleanBuilder();
        BooleanExpression exTitle = qGuestbook.title.contains(keyword);
        BooleanExpression exContent = qGuestbook.content.contains(keyword);
        BooleanExpression exAll = exTitle.or(exContent);
        BooleanExpression ex3 = qGuestbook.gno.gt(0L);
        BooleanExpression ex2 = exAll.and(ex3);
        builder.and(ex2);

        Page<Guestbook> result = guestbookRepository.findAll(builder, pageable);

        assertEquals(19, result.getTotalElements());
    }

    @Test
    public void insertDummies() {
        long before = guestbookRepository.count();
        IntStream.rangeClosed(1, 50).forEach(i -> {
            Guestbook guestbook = Guestbook.builder()
                    .title("Title..." + i)
                    .content("Content..." + i)
                    .writer("user" + (i % 10))
                    .build();

            guestbookRepository.save(guestbook);
        });

        long after = guestbookRepository.count();

        assertEquals(before + 50, after);
    }

    private void assertEntityEquals(Guestbook expected, Guestbook actual) {
        assertEquals(expected.getGno(), actual.getGno());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getContent(), actual.getContent());
        assertEquals(expected.getWriter(), actual.getWriter());
    }
}
