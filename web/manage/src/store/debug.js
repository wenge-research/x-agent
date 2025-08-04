export default({
    state: {
      questions: [],
    },
    mutations: {
      ADD_QUESTION(state, data) {
        state.questions.push(data);
      }
    },
    actions: {
      addQuestion({ commit }, data) {
        commit('ADD_QUESTION', data);
      },
    }
  });