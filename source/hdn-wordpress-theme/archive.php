<?php get_header(); ?>

		<div role="main" class="main">
			<div class="page_title">
				<div class="wrapper">
					<hgroup>
						<h1><?php single_cat_title(); ?></h1>
						<h2>Items categorised as <?php single_cat_title(); ?></h2>
					</hgroup>
				</div>
			</div><!--end page title-->
			
			<div class="main_content cf">
				<?php include_once "breadcrumbs.php"; ?>

				<div class="wrapper cf container">
					
					<div class="one_third tree_wrap">

					</div>
					
					<div class="two_thirds">
						

							<div class="content_wrap cf">
								<article class="cf">
									
									<hgroup>
										<h2>Search results for ‘<?php echo($s); ?>’</h2>
									</hgroup>

									<?php if ( have_posts() ) : while ( have_posts() ) : the_post(); ?>

									<div class="result">
										<h3><a href="<?php the_permalink(); ?>"><?php the_title(); ?></a></h3>
										<?php the_excerpt(); ?>
									</div><!-- end result-->

									<?php endwhile; ?>

									<?php else : ?>

									<p>No Results for your query. Please try again.</p>

									<?php endif; ?>

									
									
								</article>
							</div><!--end content wrap-->
						

					</div><!--end two thirds-->
					
				</div><!--end wrapper-->
			</div><!--end main_content-->
		</div><!-- end main -->
		
<?php get_footer(); ?>
