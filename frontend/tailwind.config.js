/* eslint-disable no-undef */
/* eslint-disable no-dupe-keys */
/** @type {import('tailwindcss').Config} */
const colors = require("tailwindcss/colors");

export default {
	content: [
		"./src/**/*.{html,js,jsx}",
		'./index.html',
		'./src/**/*.{js,ts,jsx,tsx}',
		'node_modules/flowbite-react/**/*.{js,jsx,ts,tsx}',
		'node_modules/flowbite/**/*.{js,jsx,ts,tsx}',

		// Path to Tremor module
		"./node_modules/@tremor/**/*.{js,ts,jsx,tsx}",

	],

	theme: {
		fontFamily: {
			jakarta: ['Plus Jakarta Sans', 'sans-serif'],
		},
		transparent: "transparent",
		current: "currentColor",
		extend: {
			colors: {
				'primary-sm':'#006400',
			},
		},
	},
	plugins: [
	],
};
