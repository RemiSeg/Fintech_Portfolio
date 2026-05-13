<template>
  <button
    :type="type"
    :disabled="disabled"
    :class="[
      'inline-flex items-center justify-center gap-2 rounded-lg px-4 py-2 text-sm font-medium transition-colors',
      'focus:outline-none focus:ring-2 focus:ring-[#1a146b]/20 focus:ring-offset-1',
      'disabled:cursor-not-allowed disabled:opacity-60',
      variantClasses,
    ]"
  >
    <slot />
  </button>
</template>

<script setup>
import { computed } from "vue";

const props = defineProps({
  variant: {
    type: String,
    default: "primary",
  },
  type: {
    type: String,
    default: "button",
  },
  disabled: {
    type: Boolean,
    default: false,
  },
});

const variantClasses = computed(() => {
  if (props.variant === "secondary") {
    return "border border-gray-300 bg-white text-[#1a146b] hover:bg-gray-50";
  }

  if (props.variant === "danger") {
    return "bg-[#ba1a1a] text-white hover:bg-red-800";
  }

  if (props.variant === "ghost") {
    return "bg-transparent text-gray-600 hover:bg-gray-50 hover:text-[#1a146b]";
  }

  return "bg-[#1a146b] text-white hover:bg-[#312e81]";
});
</script>