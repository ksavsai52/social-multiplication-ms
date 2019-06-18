package microservices.book.socialmultiplication.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.io.IOException;
import java.util.List;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import microservices.book.socialmultiplication.domain.Multiplication;
import microservices.book.socialmultiplication.domain.MultiplicationResultAttempt;
import microservices.book.socialmultiplication.domain.User;
import microservices.book.socialmultiplication.service.MultiplicationService;

@RunWith(SpringRunner.class)
@WebMvcTest(MultiplicationResultAttemptController.class)
public class MultiplicationResultAttemptControllerTest {

	@MockBean
	private MultiplicationService multiplicationService;

	@Autowired
	private MockMvc mockMvc;

	// these objects will be magically initialized by the initFields method below.
	JacksonTester<MultiplicationResultAttempt> jsonResult;
	JacksonTester<List<MultiplicationResultAttempt>> jsonResultList;

	@Before
	public void setUp() {
		JacksonTester.initFields(this, new ObjectMapper());
	}

	@Test
	public void postResultReturnCorrect() throws Exception {
		genericParameterizedTest(true);
	}

	@Test
	public void postResultReturnNotCorrect() throws Exception {
		genericParameterizedTest(false);
	}

	@Test
	public void getStatisticsTest() throws Exception {
		// given
		User user = new User("John Doe");
		Multiplication multiplication = new Multiplication(20, 30);
		MultiplicationResultAttempt multiplicationResultAttempt = new MultiplicationResultAttempt(user, multiplication,
				600, true);
		List<MultiplicationResultAttempt> recentAttempts = Lists.newArrayList(multiplicationResultAttempt,
				multiplicationResultAttempt);
		given(multiplicationService.getStatsForUser("John Doe")).willReturn(recentAttempts);

		// when
		MockHttpServletResponse response = mockMvc.perform(get("/results").param("alias", "John Doe")).andReturn()
				.getResponse();

		// then
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.getContentAsString()).isEqualTo(jsonResultList.write(recentAttempts).getJson());
	}

	@Test
	public void getResultByIdTest() throws Exception {
		// given
		User user = new User("John Doe");
		Multiplication multiplication = new Multiplication(20, 30);
		MultiplicationResultAttempt attempt = new MultiplicationResultAttempt(user, multiplication, 600, true);
		given(multiplicationService.getResultById(1l)).willReturn(attempt);

		// when
		MockHttpServletResponse response = mockMvc.perform(get("/results/1")).andReturn().getResponse();

		// then
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.getContentAsString()).isEqualTo(jsonResult.write(attempt).getJson());
	}

	private void genericParameterizedTest(final boolean correct) throws IOException, Exception {
		// given
		given(multiplicationService.checkAttempt(any(MultiplicationResultAttempt.class))).willReturn(correct);

		User user = new User("John Doe");
		Multiplication multiplication = new Multiplication(20, 30);
		MultiplicationResultAttempt resultAttempt = new MultiplicationResultAttempt(user, multiplication, 600, correct);

		// when
		MockHttpServletResponse response = mockMvc.perform(post("/results").contentType(MediaType.APPLICATION_JSON)
				.content(jsonResult.write(resultAttempt).getJson())).andReturn().getResponse();

		// then
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.getContentAsString())
				.isEqualTo(jsonResult
						.write(new MultiplicationResultAttempt(resultAttempt.getUser(),
								resultAttempt.getMultiplication(), resultAttempt.getResultAttempt(), correct))
						.getJson());
	}

}
