package com.dndgus482.guestbook.service;

import com.dndgus482.guestbook.dto.GuestbookDTO;
import com.dndgus482.guestbook.dto.PageRequestDTO;
import com.dndgus482.guestbook.dto.PageResultDTO;
import com.dndgus482.guestbook.entity.Guestbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class GuestbookServiceJUnitTests {

    @Autowired
    private GuestbookService service;

    @BeforeEach
    public void setup() {
        IntStream.rangeClosed(1, 101).forEach(i -> {
            GuestbookDTO guestbookDTO = GuestbookDTO.builder()
                    .title("Sample title..." + i)
                    .content("Sample content..." + i)
                    .writer("user" + i)
                    .build();

            service.register(guestbookDTO);
        });
    }

    @Test
    public void testRegister() {
        GuestbookDTO guestbookDTO = GuestbookDTO.builder()
                .title("Sample title...")
                .content("Sample content...")
                .writer("user0")
                .build();

        long gno = service.register(guestbookDTO);
        GuestbookDTO actual =service.read(gno);

        guestbookDTO.setGno(gno);
        assertDtoEquals(guestbookDTO, actual);
    }

    @Test
    public void testList() {
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(2)
                .size(10)
                .build();

        PageResultDTO<GuestbookDTO, Guestbook> resultDTO = service.getList(pageRequestDTO);

        assertFalse(resultDTO.isPrev());
        assertTrue(resultDTO.isNext());
        assertEquals(11, resultDTO.getTotalPage());
        assertEquals(10, resultDTO.getDtoList().size());
    }

    @Test
    public void testSearch() {
        String keyword = "1";

        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(1)
                .size(10)
                .type("tc")
                .keyword(keyword)
                .build();

        PageResultDTO<GuestbookDTO, Guestbook> resultDTO = service.getList(pageRequestDTO);

        for(GuestbookDTO dto : resultDTO.getDtoList()) {
            assertTrue(dto.getContent().contains(keyword) || dto.getTitle().contains(keyword));
        }
    }


    private void assertDtoEquals(GuestbookDTO expected, GuestbookDTO actual) {
        assertEquals(expected.getGno(), actual.getGno());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getContent(), actual.getContent());
        assertEquals(expected.getWriter(), actual.getWriter());
    }
}