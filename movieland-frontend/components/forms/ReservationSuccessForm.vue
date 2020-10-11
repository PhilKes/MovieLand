<template>
  <DialogCard title="Reservation Success!" :actions="actions">
    <v-card-text>
      Movie: {{showDetails.movieTitle}}<br/>
      Date: {{showDetails.date | formatDateTime}}<br/>
      Total Sum: {{reservation.totalSum}} $
    </v-card-text>
  </DialogCard>
</template>
<script>
  export default {
    props: {
      reservation: Object,
      showDetails: Object
    },
    data() {
      return {
        username: 'admin',
        password: 'admin123'
      }
    },
    computed: {
      actions() {
        return {
          goto: {
            flat: false,
            color: 'success',
            text: 'To my Reservation',
            handle: () => {
              this.$router.push('/users/me/reservations/' + this.reservation.id)
            }
          }
        }
      }
    }
  }
</script>
