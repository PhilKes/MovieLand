<template>
  <v-container>
    <v-row>
      <v-col>
        <movie-card :loading="loading" :movie="movie" no-link/>
      </v-col>
    </v-row>
    <v-row>
      <v-spacer/>
      <v-col v-if="!loading">
        <show-card :show="show" @submit="submitSelection" :taken-seats="takenSeats"
                   :booking="booking"/>
      </v-col>
      <v-spacer/>

    </v-row>
  </v-container>

</template>

<script>
  import MovieCard from "../../../../../components/cards/MovieCard";
  import ShowCard from "../../../../../components/cards/ShowCard";
  import ReservationSuccessForm from "../../../../../components/forms/ReservationSuccessForm";

  export default {
    name: "ShowDetails",
    components: {ShowCard, MovieCard},
    beforeCreate() {
      this.loading = true
    },
    data: () => ({
      show: null,
      movie: null,
      takenSeats: [],
      booking: false,
      loading: true,
    }),
    async fetch() {
      this.show = await this.$repos.shows.id(this.$route.params.showId);
      this.show.date = new Date(this.show.date);
      this.movie = await this.$repos.movies.id(this.show.movie.id)
      this.takenSeats = await this.$repos.seats.allOfShow(this.show.id);
      this.loading = false;
    },
    methods: {
      submitSelection(selection) {
        if (!this.$auth.loggedIn) {
          /** See layouts/default.vue for login */
          this.$events.$emit('showLogin', () => this.makeReservation(selection)) //like this
          return;
        }
        this.makeReservation(selection)
      },
      async makeReservation(selection) {
        this.booking = true;
        console.log("Reservation", this.$auth.user, selection)
        let request = {
          show_id: this.show.id,
          seats: selection.map(seat => ({
            number: seat.rowIdx * 16 + seat.colIdx,
            type: 'ADULT'
          }))
        }
        return this.$repos.reservations.add(request).then(async reservation => {
          this.takenSeats = await this.$repos.seats.allOfShow(this.show.id);
          this.booking = false;
          console.log("reservation",reservation)
          this.showReservationSuccess(reservation);
          return reservation;
        })
        //setTimeout(() => this.booking = false, 3000)

      },
      async showReservationSuccess(reservation) {
        let reservationSuccess = await this.$dialog.showAndWait(ReservationSuccessForm, {
          reservation,
          showDetails: {
            date: this.show.date,
            movieTitle: this.movie.name
          }
        }).then(resp => resp);
        if (!reservationSuccess)
          throw null;
        return;
      }
    }
  }
</script>

<style scoped>

</style>
