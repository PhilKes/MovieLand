import moment from "moment";

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
      trailer(id){
        return axios.$get(`/movies/trailer/${id}`)
      },
      add(movie) {
        return axios.$post(`/movies`, movie)
      },
      remove(movId){
        return axios.$delete(`/movies/${movId}`)
      }
    },
    tmdb:{
      search(search){
        return axios.$get(`/movies/tmdb`,{params:{name:search}})
      },
      topMovies(){
        return axios.$get(`/movies/tmdb/top`)
      }
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
      },
      showInfos(date){
        return axios.$get(`/shows/infos`,{params:{date: moment(date).format("YYYY-MM-DD")}})
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
