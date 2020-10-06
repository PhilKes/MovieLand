export default (ctx, inject) => {
  const axios = (ctx.$axios)

  const repositories = {
    movies: {
      all() {
        return axios.$get(`/movies`)
      },

      id(id) {
        return axios.$get(`/movies/${id}`)
      },

    },
    shows: {
      all() {
        return axios.$get(`/shows`)
      },

      id(id) {
        return axios.$get(`/shows/${id}`)
      },
      showsOfMovieWeek(movId) {
        return axios.$get(`/shows/movies/${movId}`)
      }
    },
    seats: {
      allOfShow(showId) {
        return axios.$get(`/seats/shows/${showId}`);
      }
    },
    reservations: {
      add(request) {
        return axios.$post(`/reservations`, request);
      },
      userReservation(id) {
        return axios.get(`/reservations/me/id/${id}`).then(resp => resp.data)
      },
      allUserReservations() {
        return axios.get(`/reservations/me/info`).then(resp => resp.data)
      }
    }
  }
  //...

  inject('repos', repositories)
  ctx.$repos = repositories;
}
