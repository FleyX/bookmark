<template>
  <div class="home">
    {{ count }}
  </div>
</template>

<script>
// @ is an alias to /src
import { mapState } from "vuex";
import * as types from "../store/type";

export default {
  name: "Home",
  computed: {
    ...mapState([types.COUNT])
  },
  components: {}
};
</script>
